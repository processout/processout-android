package com.processout.sdk.ui.card.tokenization

import android.app.Application
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.R
import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.dispatcher.card.tokenization.PODefaultCardTokenizationEventDispatcher
import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.event.POCardTokenizationEvent.*
import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.response.*
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.core.POFailure.Code.Cancelled
import com.processout.sdk.core.POFailure.Code.Generic
import com.processout.sdk.core.POFailure.GenericCode.*
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.getOrNull
import com.processout.sdk.core.logger.POLogAttribute
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import com.processout.sdk.ui.base.BaseInteractor
import com.processout.sdk.ui.card.scanner.recognition.POScannedCard
import com.processout.sdk.ui.card.tokenization.CardTokenizationCompletion.*
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent.*
import com.processout.sdk.ui.card.tokenization.CardTokenizationInteractorState.*
import com.processout.sdk.ui.card.tokenization.CardTokenizationSideEffect.CardScanner
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration.BillingAddressConfiguration.CollectionMode.*
import com.processout.sdk.ui.card.tokenization.delegate.CardTokenizationEligibilityRequest
import com.processout.sdk.ui.card.tokenization.delegate.CardTokenizationEligibilityResponse
import com.processout.sdk.ui.card.tokenization.delegate.POCardTokenizationEligibility
import com.processout.sdk.ui.card.tokenization.delegate.POCardTokenizationEligibility.Eligible
import com.processout.sdk.ui.card.tokenization.delegate.POCardTokenizationEligibility.NotEligible
import com.processout.sdk.ui.core.state.POAvailableValue
import com.processout.sdk.ui.shared.extension.currentAppLocale
import com.processout.sdk.ui.shared.extension.findBy
import com.processout.sdk.ui.shared.extension.orElse
import com.processout.sdk.ui.shared.filter.CardExpirationInputFilter
import com.processout.sdk.ui.shared.filter.CardNumberInputFilter
import com.processout.sdk.ui.shared.provider.CardSchemeProvider
import com.processout.sdk.ui.shared.provider.address.AddressSpecification
import com.processout.sdk.ui.shared.provider.address.AddressSpecification.AddressUnit
import com.processout.sdk.ui.shared.provider.address.AddressSpecificationProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

internal class CardTokenizationInteractor(
    private val app: Application,
    private var configuration: POCardTokenizationConfiguration,
    private val cardsRepository: POCardsRepository,
    private val cardSchemeProvider: CardSchemeProvider,
    private val addressSpecificationProvider: AddressSpecificationProvider,
    private val legacyEventDispatcher: PODefaultCardTokenizationEventDispatcher?, // TODO: remove before next major release.
    private val eventDispatcher: POEventDispatcher = POEventDispatcher
) : BaseInteractor() {

    private companion object {
        const val IIN_LENGTH = 8
        const val EXPIRATION_DATE_PART_LENGTH = 2
        const val CARD_SCANNER_DELAY_MS = 350L
    }

    private data class Expiration(
        val month: Int,
        val year: Int
    )

    private val _completion = MutableStateFlow<CardTokenizationCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    private val _sideEffects = Channel<CardTokenizationSideEffect>()
    val sideEffects = _sideEffects.receiveAsFlow()

    private val cardNumberInputFilter = CardNumberInputFilter()
    private val cardExpirationInputFilter = CardExpirationInputFilter()

    private var issuerInformationJob: Job? = null

    private var latestEligibilityRequest: CardTokenizationEligibilityRequest? = null
    private var latestPreferredSchemeRequest: POCardTokenizationPreferredSchemeRequest? = null
    private var latestShouldContinueRequest: POCardTokenizationShouldContinueRequest? = null

    //region Initialization

    fun start() {
        if (_state.value.started) {
            return
        }
        _state.update { it.copy(started = true) }
        interactorScope.launch {
            POLogger.info("Starting card tokenization.")
            dispatch(WillStart)
            handleCompletion()
            shouldContinueOnFailure()
            collectFailure()
            collectEligibility()
            collectPreferredScheme()
            initAddressFields()
            POLogger.info("Card tokenization is started: waiting for user input.")
            dispatch(DidStart)
        }
    }

    fun start(configuration: POCardTokenizationConfiguration) {
        if (_state.value.started) {
            return
        }
        this.configuration = configuration
        _state.update { initState() }
        start()
    }

    fun reset() {
        cancelProcessing()
        _completion.update { Awaiting }
        _state.update { initState() }
    }

    private fun cancelProcessing() {
        interactorScope.coroutineContext.cancelChildren()
        issuerInformationJob = null
        latestEligibilityRequest = null
        latestPreferredSchemeRequest = null
        latestShouldContinueRequest = null
    }

    private fun initState() = CardTokenizationInteractorState(
        cardFields = cardFields(),
        addressFields = emptyList(),
        preferredSchemeField = Field(
            id = FieldId.PREFERRED_SCHEME,
            shouldCollect = false
        ),
        saveCardField = Field(
            id = FieldId.SAVE_CARD,
            value = TextFieldValue(text = "false"),
            shouldCollect = configuration.savingAllowed
        ),
        focusedFieldId = CardFieldId.NUMBER,
        pendingFocusedFieldId = null,
        primaryActionId = ActionId.SUBMIT,
        secondaryActionId = ActionId.CANCEL,
        cardScannerActionId = ActionId.CARD_SCANNER
    )

    private fun cardFields(): List<Field> = mutableListOf(
        Field(id = CardFieldId.NUMBER),
        Field(id = CardFieldId.EXPIRATION),
        Field(
            id = CardFieldId.CVC,
            shouldCollect = configuration.cvcRequired
        ),
        Field(
            id = CardFieldId.CARDHOLDER,
            shouldCollect = configuration.cardholderNameRequired
        )
    )

    private suspend fun initAddressFields() {
        val countryCodes = addressSpecificationProvider.countryCodes()
        _state.update { it.copy(addressFields = listOf(countryField(countryCodes))) }
        updateAddressSpecification()
    }

    private fun countryField(countryCodes: Set<String>): Field {
        val supportedCountryCodes = configuration.billingAddress.countryCodes
            ?.let { configurationCountryCodes ->
                configurationCountryCodes.filter { countryCodes.contains(it) }
            }.orElse { countryCodes }

        val currentAppLocale = app.currentAppLocale()
        val availableValues = supportedCountryCodes.map {
            POAvailableValue(
                value = it,
                text = Locale(String(), it).getDisplayCountry(currentAppLocale)
            )
        }.sortedBy { it.text }

        var defaultCountryCode: String = configuration.billingAddress.defaultAddress?.countryCode
            ?: app.currentAppLocale().country
        if (!supportedCountryCodes.contains(defaultCountryCode)) {
            defaultCountryCode = availableValues.first().value
        }
        return Field(
            id = AddressFieldId.COUNTRY,
            value = TextFieldValue(text = defaultCountryCode),
            availableValues = availableValues,
            shouldCollect = configuration.billingAddress.mode != Never && supportedCountryCodes.isNotEmpty()
        )
    }

    //endregion

    //region Events

    fun onEvent(event: CardTokenizationEvent) {
        when (event) {
            is FieldValueChanged -> updateFieldValue(event.id, event.value)
            is FieldFocusChanged -> updateFieldFocus(event.id, event.isFocused)
            is Action -> when (event.id) {
                ActionId.SUBMIT -> submit()
                ActionId.CANCEL -> cancel()
                ActionId.CARD_SCANNER -> startCardScanner()
            }
            is CardScannerResult -> handle(event)
            is Dismiss -> POLogger.info("Dismissed: %s", event.failure)
        }
    }

    private fun startCardScanner() {
        interactorScope.launch {
            rememberAndClearFieldFocus()
            delay(CARD_SCANNER_DELAY_MS)
            _sideEffects.send(CardScanner)
        }
    }

    private fun handle(event: CardScannerResult) {
        event.card?.let { updateCardFields(it) }
        restoreFieldFocus()
    }

    //endregion

    //region Update Field

    private fun updateFieldValue(id: String, value: TextFieldValue) {
        val previousValue = allFields().find { it.id == id }?.value ?: TextFieldValue()
        val isTextChanged = value.text != previousValue.text
        _state.update {
            it.copy(
                cardFields = it.cardFields.map { field ->
                    updatedField(id, value, field, isTextChanged)
                },
                addressFields = it.addressFields.map { field ->
                    updatedField(id, value, field, isTextChanged)
                },
                preferredSchemeField = updatedField(id, value, it.preferredSchemeField, isTextChanged),
                saveCardField = updatedField(id, value, it.saveCardField, isTextChanged)
            )
        }
        if (isTextChanged) {
            POLogger.debug(message = "Field is edited by the user: %s", id)
            dispatch(ParametersChanged)
            if (isSubmitAllowed()) {
                _state.update {
                    it.copy(
                        submitAllowed = true,
                        errorMessage = null
                    )
                }
            }
            when (id) {
                CardFieldId.NUMBER -> updateIssuerInformation(
                    cardNumber = value.text,
                    previousCardNumber = previousValue.text
                )
                AddressFieldId.COUNTRY -> updateAddressSpecification()
            }
        }
    }

    private fun updatedField(
        id: String,
        value: TextFieldValue,
        field: Field,
        isTextChanged: Boolean
    ): Field =
        if (field.id == id) {
            if (isTextChanged) {
                field.copy(value = value, isValid = true)
            } else {
                field.copy(value = value)
            }
        } else field

    private fun updateAllFields(enabled: Boolean) {
        _state.update {
            it.copy(
                cardFields = it.cardFields.map { field ->
                    field.copy(enabled = enabled)
                },
                addressFields = it.addressFields.map { field ->
                    field.copy(enabled = enabled)
                },
                preferredSchemeField = it.preferredSchemeField.copy(enabled = enabled),
                saveCardField = it.saveCardField.copy(enabled = enabled)
            )
        }
    }

    private fun updateCardFields(card: POScannedCard) {
        POLogger.debug("Updating card field values with the scanned card: $card.")
        updateFieldValue(
            id = CardFieldId.NUMBER,
            value = cardNumberInputFilter.filter(
                TextFieldValue(
                    text = card.number,
                    selection = TextRange(index = card.number.length)
                )
            )
        )
        card.expiration?.let {
            updateFieldValue(
                id = CardFieldId.EXPIRATION,
                value = cardExpirationInputFilter.filter(
                    TextFieldValue(
                        text = it.formatted,
                        selection = TextRange(index = it.formatted.length)
                    )
                )
            )
        }
        card.cardholderName?.let {
            updateFieldValue(
                id = CardFieldId.CARDHOLDER,
                value = TextFieldValue(
                    text = it,
                    selection = TextRange(index = it.length)
                )
            )
        }
    }

    private fun updateFieldFocus(id: String, isFocused: Boolean) {
        if (isFocused) {
            _state.update { it.copy(focusedFieldId = id) }
        }
    }

    private fun rememberAndClearFieldFocus() {
        _state.update {
            val focusedFieldId = it.focusedFieldId
            it.copy(
                focusedFieldId = null,
                pendingFocusedFieldId = focusedFieldId
            )
        }
    }

    private fun restoreFieldFocus() {
        _state.update {
            val pendingFocusedFieldId = it.pendingFocusedFieldId
            it.copy(
                focusedFieldId = pendingFocusedFieldId,
                pendingFocusedFieldId = null
            )
        }
    }

    //endregion

    //region Issuer Information

    private fun updateIssuerInformation(cardNumber: String, previousCardNumber: String) {
        val iin = iin(cardNumber)
        if (iin == iin(previousCardNumber)) {
            return
        }
        val localIssuerInformation = localIssuerInformation(iin)
        _state.update {
            it.copy(
                submitAllowed = areAllFieldsValid(),
                errorMessage = null,
                issuerInformation = localIssuerInformation,
                eligibility = Eligible()
            )
        }
        updatePreferredScheme(scheme = localIssuerInformation?.scheme)
        if (iin.length == IIN_LENGTH) {
            updateIssuerInformation(iin)
        }
    }

    private fun iin(cardNumber: String) = cardNumber.take(IIN_LENGTH)

    private fun localIssuerInformation(iin: String) =
        cardSchemeProvider.scheme(iin)?.let { scheme ->
            POCardIssuerInformation(scheme = scheme)
        }

    private fun updateIssuerInformation(iin: String) {
        issuerInformationJob?.cancel()
        issuerInformationJob = interactorScope.launch {
            fetchIssuerInformation(iin)?.let { issuerInformation ->
                _state.update { it.copy(issuerInformation = issuerInformation) }
                requestEligibility(iin, issuerInformation)
            }
        }
    }

    private suspend fun fetchIssuerInformation(iin: String) =
        cardsRepository.fetchIssuerInformation(iin)
            .onFailure {
                POLogger.info(
                    message = "Failed to fetch issuer information: %s", it,
                    attributes = mapOf(POLogAttribute.IIN to iin)
                )
            }.getOrNull()

    //endregion

    //region Eligibility

    private fun requestEligibility(
        iin: String,
        issuerInformation: POCardIssuerInformation
    ) {
        interactorScope.launch {
            val request = CardTokenizationEligibilityRequest(
                iin = iin,
                issuerInformation = issuerInformation
            )
            latestEligibilityRequest = request
            eventDispatcher.send(request)
            POLogger.info("Requested to evaluate card eligibility: [iin=%s] [issuerInformation=%s]", iin, issuerInformation)
        }
    }

    private fun collectEligibility() {
        eventDispatcher.subscribeForResponse<CardTokenizationEligibilityResponse>(
            coroutineScope = interactorScope
        ) { response ->
            if (response.uuid == latestEligibilityRequest?.uuid) {
                latestEligibilityRequest = null
                handleEligibility(response.eligibility)
            }
        }
    }

    private fun handleEligibility(eligibility: POCardTokenizationEligibility) {
        _state.update { it.copy(eligibility = eligibility) }
        POLogger.info("Card eligibility: %s", eligibility)
        if (eligibility is NotEligible) {
            val errorMessage = eligibility.failure?.localizedMessage
                ?: app.getString(R.string.po_card_tokenization_error_eligibility)
            _state.update {
                it.copy(
                    cardFields = it.cardFields.map { field ->
                        validatedField(
                            field = field,
                            invalidFieldIds = setOf(CardFieldId.NUMBER)
                        )
                    },
                    focusedFieldId = CardFieldId.NUMBER,
                    submitAllowed = false,
                    pendingSubmit = false,
                    submitting = false,
                    errorMessage = errorMessage
                )
            }
            updateAllFields(enabled = true)
        }
        val eligibleSchemes = _state.value.eligibleSchemes
        if (eligibleSchemes.size > 1) {
            _state.value.issuerInformation?.let {
                requestPreferredScheme(issuerInformation = it)
                return
            }
        } else {
            eligibleSchemes.firstOrNull()?.let {
                updatePreferredScheme(scheme = it)
            }
        }
        if (_state.value.pendingSubmit) {
            _state.update { it.copy(pendingSubmit = false) }
            submit()
        }
    }

    private val CardTokenizationInteractorState.eligibleSchemes: List<String>
        get() = when (eligibility) {
            is Eligible ->
                if (eligibility.scheme != null) {
                    listOf(eligibility.scheme)
                } else {
                    listOfNotNull(
                        issuerInformation?.scheme,
                        issuerInformation?.coScheme
                    )
                }
            is NotEligible -> emptyList()
        }

    //endregion

    //region Preferred Scheme

    private fun requestPreferredScheme(issuerInformation: POCardIssuerInformation) {
        interactorScope.launch {
            val request = POCardTokenizationPreferredSchemeRequest(issuerInformation)
            latestPreferredSchemeRequest = request
            if (legacyEventDispatcher?.subscribedForPreferredSchemeRequest() == true) {
                legacyEventDispatcher.send(request)
            } else {
                eventDispatcher.send(request)
            }
            POLogger.info("Requested to choose preferred scheme by issuer information: %s", issuerInformation)
        }
    }

    private fun collectPreferredScheme() {
        interactorScope.launch {
            legacyEventDispatcher?.preferredSchemeResponse?.collect { response ->
                handlePreferredScheme(response)
            }
        }
        eventDispatcher.subscribeForResponse<POCardTokenizationPreferredSchemeResponse>(
            coroutineScope = interactorScope
        ) { response ->
            handlePreferredScheme(response)
        }
    }

    private fun handlePreferredScheme(response: POCardTokenizationPreferredSchemeResponse) {
        if (response.uuid == latestPreferredSchemeRequest?.uuid) {
            latestPreferredSchemeRequest = null
            updatePreferredScheme(scheme = response.preferredScheme)
            if (_state.value.pendingSubmit) {
                _state.update { it.copy(pendingSubmit = false) }
                submit()
            }
        }
    }

    private fun updatePreferredScheme(scheme: String?) {
        val eligibleSchemes = _state.value.eligibleSchemes.mapNotNull { availableScheme(scheme = it) }
        val preferredScheme = availableScheme(scheme)?.let {
            if (it in eligibleSchemes) it else {
                POLogger.warn("Preferred scheme is not eligible: %s", it.value)
                eligibleSchemes.firstOrNull()
            }
        }
        _state.update {
            it.copy(
                preferredSchemeField = it.preferredSchemeField.copy(
                    value = TextFieldValue(text = preferredScheme?.value ?: String()),
                    availableValues = eligibleSchemes,
                    shouldCollect = configuration.preferredScheme != null && eligibleSchemes.size > 1
                )
            )
        }
        POLogger.info("Preferred scheme updated: %s", preferredScheme?.value)
    }

    private fun availableScheme(scheme: String?): POAvailableValue? =
        POCardScheme::rawValue.findBy(scheme)?.let {
            POAvailableValue(
                value = it.rawValue,
                text = it.displayName
            )
        }

    //endregion

    //region Address Specification

    private fun updateAddressSpecification() {
        _state.value.addressFields.find { it.id == AddressFieldId.COUNTRY }?.let { countryField ->
            interactorScope.launch {
                val countryCode = countryField.value.text
                val specification = addressSpecificationProvider.specification(countryCode)
                val addressFields = mutableListOf(countryField)
                addressFields.addAll(addressFields(countryCode, specification))
                _state.update {
                    it.copy(
                        addressFields = addressFields,
                        addressSpecification = specification
                    )
                }
            }
        }
    }

    private fun addressFields(countryCode: String, specification: AddressSpecification): List<Field> {
        val currentAddress = currentAddress()
        val defaultAddress = configuration.billingAddress.defaultAddress
        val address1 = currentAddress.address1 ?: defaultAddress?.address1 ?: String()
        val address2 = currentAddress.address2 ?: defaultAddress?.address2 ?: String()
        val city = currentAddress.city ?: defaultAddress?.city ?: String()
        val state = currentAddress.state ?: defaultAddress?.state ?: String()
        val postalCode = currentAddress.zip ?: defaultAddress?.zip ?: String()
        val fields = mutableListOf<Field>()
        specification.units.forEach { unit ->
            when (unit) {
                AddressUnit.street -> {
                    val streetFields = listOf(
                        Field(
                            id = AddressFieldId.ADDRESS_1,
                            value = TextFieldValue(
                                text = address1,
                                selection = TextRange(address1.length)
                            ),
                            shouldCollect = shouldCollect(unit, countryCode)
                        ),
                        Field(
                            id = AddressFieldId.ADDRESS_2,
                            value = TextFieldValue(
                                text = address2,
                                selection = TextRange(address2.length)
                            ),
                            shouldCollect = shouldCollect(unit, countryCode)
                        )
                    )
                    fields.addAll(streetFields)
                }
                AddressUnit.city -> Field(
                    id = AddressFieldId.CITY,
                    value = TextFieldValue(
                        text = city,
                        selection = TextRange(city.length)
                    ),
                    shouldCollect = shouldCollect(unit, countryCode)
                ).also { fields.add(it) }
                AddressUnit.state -> Field(
                    id = AddressFieldId.STATE,
                    value = TextFieldValue(
                        text = state,
                        selection = TextRange(state.length)
                    ),
                    shouldCollect = shouldCollect(unit, countryCode)
                ).also { fields.add(it) }
                AddressUnit.postcode -> Field(
                    id = AddressFieldId.POSTAL_CODE,
                    value = TextFieldValue(
                        text = postalCode,
                        selection = TextRange(postalCode.length)
                    ),
                    shouldCollect = shouldCollect(unit, countryCode)
                ).also { fields.add(it) }
            }
        }
        val fieldIds = fields.map { it.id }
        val nonSpecFields = _state.value.addressFields.filter {
            it.id != AddressFieldId.COUNTRY && !fieldIds.contains(it.id)
        }.map { it.copy(shouldCollect = false) }
        return fields + nonSpecFields
    }

    private fun currentAddress(): POContact {
        var countryCode: String? = null
        var address1: String? = null
        var address2: String? = null
        var city: String? = null
        var state: String? = null
        var postalCode: String? = null
        _state.value.addressFields.forEach {
            when (it.id) {
                AddressFieldId.COUNTRY -> countryCode = it.value.text
                AddressFieldId.ADDRESS_1 -> address1 = it.value.text
                AddressFieldId.ADDRESS_2 -> address2 = it.value.text
                AddressFieldId.CITY -> city = it.value.text
                AddressFieldId.STATE -> state = it.value.text
                AddressFieldId.POSTAL_CODE -> postalCode = it.value.text
            }
        }
        return POContact(
            countryCode = countryCode,
            address1 = address1,
            address2 = address2,
            city = city,
            state = state,
            zip = postalCode
        )
    }

    private fun shouldCollect(unit: AddressUnit, countryCode: String): Boolean =
        when (configuration.billingAddress.mode) {
            Never -> false
            Automatic -> when (unit) {
                AddressUnit.postcode -> {
                    val supportedCountryCodes = setOf("US", "GB", "CA")
                    supportedCountryCodes.contains(countryCode)
                }
                else -> false
            }
            Full -> true
        }

    //endregion

    //region Submit

    private fun submit() {
        if (_state.value.pendingSubmit) {
            return
        }
        if (issuerInformationJob?.isActive == true || latestEligibilityRequest != null) {
            _state.update {
                it.copy(
                    pendingSubmit = true,
                    submitting = true,
                    errorMessage = null
                )
            }
            updateAllFields(enabled = false)
            return
        }
        if (!isSubmitAllowed()) {
            POLogger.debug("Ignored attempt to tokenize the card with invalid values.")
            return
        }
        _state.update {
            it.copy(
                submitAllowed = true,
                submitting = true,
                errorMessage = null
            )
        }
        updateAllFields(enabled = false)
        tokenize(tokenizationRequest())
    }

    private fun isSubmitAllowed() = _state.value.eligibility is Eligible && areAllFieldsValid()

    private fun areAllFieldsValid(): Boolean = allFields().all { it.isValid }

    private fun allFields(): List<Field> = with(_state.value) {
        cardFields + addressFields + preferredSchemeField + saveCardField
    }

    //endregion

    //region Tokenization Request

    private fun tokenizationRequest(): POCardTokenizationRequest {
        var cardNumber = String()
        var expiration = String()
        var cvc = String()
        var cardholderName = String()
        _state.value.cardFields.forEach {
            when (it.id) {
                CardFieldId.NUMBER -> cardNumber = it.value.text
                CardFieldId.EXPIRATION -> expiration = it.value.text
                CardFieldId.CVC -> cvc = it.value.text
                CardFieldId.CARDHOLDER -> cardholderName = it.value.text
            }
        }
        val parsedExpiration = parseExpiration(expiration)
        return POCardTokenizationRequest(
            number = cardNumber,
            expMonth = parsedExpiration.month,
            expYear = parsedExpiration.year,
            cvc = cvc,
            name = cardholderName,
            preferredScheme = _state.value.preferredSchemeField.value.text,
            contact = contact(),
            metadata = configuration.metadata
        )
    }

    private fun parseExpiration(value: String): Expiration {
        val dateParts = value.chunked(EXPIRATION_DATE_PART_LENGTH)
        return Expiration(
            month = dateParts.getOrNull(0)?.toIntOrNull() ?: 0,
            year = dateParts.getOrNull(1)?.toIntOrNull() ?: 0
        )
    }

    private fun contact(): POContact {
        var countryCode = String()
        var address1 = String()
        var address2 = String()
        var city = String()
        var state = String()
        var postalCode = String()
        val defaultAddress = configuration.billingAddress.defaultAddress
        _state.value.addressFields.forEach {
            when (it.id) {
                AddressFieldId.COUNTRY -> countryCode = addressValue(it, defaultAddress?.countryCode)
                AddressFieldId.ADDRESS_1 -> address1 = addressValue(it, defaultAddress?.address1)
                AddressFieldId.ADDRESS_2 -> address2 = addressValue(it, defaultAddress?.address2)
                AddressFieldId.CITY -> city = addressValue(it, defaultAddress?.city)
                AddressFieldId.STATE -> state = addressValue(it, defaultAddress?.state)
                AddressFieldId.POSTAL_CODE -> postalCode = addressValue(it, defaultAddress?.zip)
            }
        }
        return POContact(
            countryCode = countryCode,
            address1 = address1,
            address2 = address2,
            city = city,
            state = state,
            zip = postalCode
        )
    }

    private fun addressValue(field: Field, defaultValue: String?): String {
        if (!configuration.billingAddress.attachDefaultsToPaymentMethod) {
            return if (field.shouldCollect) field.value.text else String()
        }
        return field.value.text.ifBlank { defaultValue ?: String() }
    }

    //endregion

    //region Tokenization

    private fun tokenize(request: POCardTokenizationRequest) {
        POLogger.info(message = "Submitting card information.")
        dispatch(WillTokenize)
        interactorScope.launch {
            cardsRepository.tokenize(request)
                .onSuccess { card ->
                    _state.update {
                        it.copy(
                            tokenizedCard = card,
                            focusedFieldId = null
                        )
                    }
                    POLogger.info(
                        message = "Card tokenized successfully.",
                        attributes = mapOf(POLogAttribute.CARD_ID to card.id)
                    )
                    dispatch(DidTokenize(card))
                    requestToProcessTokenizedCard(card)
                }.onFailure {
                    requestIfShouldContinue(failure = it)
                }
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun requestToProcessTokenizedCard(card: POCard) {
        val request = POCardTokenizationProcessingRequest(
            card = card,
            saveCard = _state.value.saveCardField.value.text.toBooleanStrictOrNull() ?: false
        )
        if (legacyEventDispatcher?.subscribedForProcessTokenizedCard() == true) {
            legacyEventDispatcher.processTokenizedCard(card)
        } else if (legacyEventDispatcher?.subscribedForProcessTokenizedCardRequest() == true) {
            legacyEventDispatcher.processTokenizedCardRequest(request)
        } else {
            eventDispatcher.send(request)
        }
        POLogger.info(
            message = "Requested to process tokenized card.",
            attributes = mapOf(POLogAttribute.CARD_ID to card.id)
        )
    }

    private fun handleCompletion() {
        interactorScope.launch {
            legacyEventDispatcher?.completion?.collect { result ->
                result.onSuccess {
                    _state.value.tokenizedCard?.let { card ->
                        complete(Success(card))
                    }.orElse {
                        val failure = ProcessOutResult.Failure(
                            code = Generic(),
                            message = "Completion is called with Success via dispatcher before card is tokenized."
                        )
                        requestIfShouldContinue(failure)
                    }
                }.onFailure {
                    requestIfShouldContinue(failure = it)
                }
            }
        }
        eventDispatcher.subscribeForResponse<POCardTokenizationProcessingResponse>(
            coroutineScope = interactorScope
        ) { response ->
            response.result
                .onSuccess { card ->
                    complete(Success(card))
                }.onFailure {
                    requestIfShouldContinue(failure = it)
                }
        }
    }

    private fun complete(success: Success) {
        POLogger.info(
            message = "Completed successfully.",
            attributes = mapOf(POLogAttribute.CARD_ID to success.card.id)
        )
        dispatch(DidComplete)
        _completion.update { success }
    }

    private fun requestIfShouldContinue(failure: ProcessOutResult.Failure) {
        interactorScope.launch {
            val request = POCardTokenizationShouldContinueRequest(failure)
            latestShouldContinueRequest = request
            if (legacyEventDispatcher?.subscribedForShouldContinueRequest() == true) {
                legacyEventDispatcher.send(request)
            } else {
                eventDispatcher.send(request)
            }
            POLogger.info("Requested to decide whether the flow should continue or complete after the failure: %s", failure)
        }
    }

    private fun shouldContinueOnFailure() {
        interactorScope.launch {
            legacyEventDispatcher?.shouldContinueResponse?.collect { response ->
                handleShouldContinue(response)
            }
        }
        eventDispatcher.subscribeForResponse<POCardTokenizationShouldContinueResponse>(
            coroutineScope = interactorScope
        ) { response ->
            handleShouldContinue(response)
        }
    }

    private fun handleShouldContinue(response: POCardTokenizationShouldContinueResponse) {
        if (response.uuid == latestShouldContinueRequest?.uuid) {
            latestShouldContinueRequest = null
            if (response.shouldContinue) {
                handle(response.failure)
            } else {
                POLogger.info("Completed after the failure: %s", response.failure)
                _completion.update { Failure(response.failure) }
            }
        }
    }

    //endregion

    //region Handle Failure

    private fun handle(failure: ProcessOutResult.Failure) {
        val invalidFieldIds = mutableSetOf<String>()
        var errorMessage = when (val code = failure.code) {
            is Generic -> when (code.genericCode) {
                requestInvalidCard,
                cardInvalid -> {
                    invalidFieldIds.addAll(
                        listOf(
                            CardFieldId.NUMBER,
                            CardFieldId.EXPIRATION,
                            CardFieldId.CVC,
                            CardFieldId.CARDHOLDER
                        )
                    )
                    app.getString(R.string.po_card_tokenization_error_card)
                }
                cardInvalidNumber,
                cardMissingNumber -> {
                    invalidFieldIds.add(CardFieldId.NUMBER)
                    app.getString(R.string.po_card_tokenization_error_card_number)
                }
                cardMissingExpiry,
                cardInvalidExpiryDate,
                cardInvalidExpiryMonth,
                cardInvalidExpiryYear -> {
                    invalidFieldIds.add(CardFieldId.EXPIRATION)
                    app.getString(R.string.po_card_tokenization_error_card_expiration)
                }
                cardBadTrackData -> {
                    invalidFieldIds.addAll(
                        listOf(
                            CardFieldId.EXPIRATION,
                            CardFieldId.CVC
                        )
                    )
                    app.getString(R.string.po_card_tokenization_error_track_data)
                }
                cardMissingCvc,
                cardInvalidCvc,
                cardFailedCvc,
                cardFailedCvcAndAvs -> {
                    invalidFieldIds.add(CardFieldId.CVC)
                    app.getString(R.string.po_card_tokenization_error_cvc)
                }
                cardInvalidName -> {
                    invalidFieldIds.add(CardFieldId.CARDHOLDER)
                    app.getString(R.string.po_card_tokenization_error_cardholder)
                }
                else -> app.getString(R.string.po_card_tokenization_error_generic)
            }
            Cancelled -> null
            else -> app.getString(R.string.po_card_tokenization_error_generic)
        }
        failure.localizedMessage?.let { errorMessage = it }
        handle(failure, invalidFieldIds, errorMessage)
    }

    private fun handle(
        failure: ProcessOutResult.Failure,
        invalidFieldIds: Set<String>,
        errorMessage: String?
    ) {
        val cardFields = _state.value.cardFields.map { field ->
            validatedField(field, invalidFieldIds)
        }
        val addressFields = _state.value.addressFields.map { field ->
            validatedField(field, invalidFieldIds)
        }
        val preferredSchemeField = validatedField(_state.value.preferredSchemeField, invalidFieldIds)
        val saveCardField = validatedField(_state.value.saveCardField, invalidFieldIds)
        val allFields = cardFields + addressFields + preferredSchemeField + saveCardField
        val firstInvalidFieldId = allFields.find { !it.isValid }?.id
        _state.update { state ->
            state.copy(
                cardFields = cardFields,
                addressFields = addressFields,
                preferredSchemeField = preferredSchemeField,
                saveCardField = saveCardField,
                focusedFieldId = firstInvalidFieldId ?: state.focusedFieldId,
                submitAllowed = allFields.all { it.isValid },
                submitting = false,
                errorMessage = errorMessage
            )
        }
        updateAllFields(enabled = true)
        POLogger.info(message = "Recovered after the failure: %s", failure)
    }

    private fun validatedField(field: Field, invalidFieldIds: Set<String>): Field =
        if (invalidFieldIds.contains(field.id)) {
            field.copy(
                isValid = false,
                value = field.value.copy(selection = TextRange(field.value.text.length))
            )
        } else field

    //endregion

    private fun cancel() {
        _completion.update {
            Failure(
                ProcessOutResult.Failure(
                    code = Cancelled,
                    message = "Cancelled by the user with secondary cancel action."
                ).also { POLogger.info("Cancelled: %s", it) }
            )
        }
    }

    private fun dispatch(event: POCardTokenizationEvent) {
        interactorScope.launch {
            legacyEventDispatcher?.send(event)
            eventDispatcher.send(event)
        }
    }

    private fun collectFailure() {
        interactorScope.launch {
            _completion.collect {
                if (it is Failure) {
                    cancelProcessing()
                    POLogger.warn("%s", it.failure)
                }
            }
        }
    }
}
