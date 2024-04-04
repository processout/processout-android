package com.processout.sdk.ui.card.tokenization.v2

import android.app.Application
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.api.dispatcher.card.tokenization.PODefaultCardTokenizationEventDispatcher
import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.event.POCardTokenizationEvent.*
import com.processout.sdk.api.model.request.POCardTokenizationPreferredSchemeRequest
import com.processout.sdk.api.model.request.POCardTokenizationRequest
import com.processout.sdk.api.model.request.POCardTokenizationShouldContinueRequest
import com.processout.sdk.api.model.request.POContact
import com.processout.sdk.api.model.response.POCardIssuerInformation
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.getOrNull
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.onFailure
import com.processout.sdk.ui.base.BaseInteractor
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration.BillingAddressConfiguration.CollectionMode.*
import com.processout.sdk.ui.card.tokenization.v2.CardTokenizationCompletion.Awaiting
import com.processout.sdk.ui.card.tokenization.v2.CardTokenizationEvent.*
import com.processout.sdk.ui.card.tokenization.v2.CardTokenizationInteractorState.*
import com.processout.sdk.ui.core.state.POAvailableValue
import com.processout.sdk.ui.shared.extension.currentAppLocale
import com.processout.sdk.ui.shared.extension.orElse
import com.processout.sdk.ui.shared.provider.CardSchemeProvider
import com.processout.sdk.ui.shared.provider.address.AddressSpecification
import com.processout.sdk.ui.shared.provider.address.AddressSpecification.AddressUnit
import com.processout.sdk.ui.shared.provider.address.AddressSpecificationProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

internal class CardTokenizationInteractor(
    private val app: Application,
    private val configuration: POCardTokenizationConfiguration,
    private val cardsRepository: POCardsRepository,
    private val cardSchemeProvider: CardSchemeProvider,
    private val addressSpecificationProvider: AddressSpecificationProvider,
    private val eventDispatcher: PODefaultCardTokenizationEventDispatcher
) : BaseInteractor() {

    private companion object {
        const val IIN_LENGTH = 6
        const val EXPIRATION_DATE_PART_LENGTH = 2
        const val LOG_ATTRIBUTE_IIN = "IIN"
        const val LOG_ATTRIBUTE_CARD_ID = "CardId"
    }

    private data class Expiration(
        val month: Int,
        val year: Int
    )

    private val _completion = MutableStateFlow<CardTokenizationCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(init())
    val state = _state.asStateFlow()

    private var latestPreferredSchemeRequest: POCardTokenizationPreferredSchemeRequest? = null
    private var latestShouldContinueRequest: POCardTokenizationShouldContinueRequest? = null

    private var issuerInformationJob: Job? = null

    init {
        interactorScope.launch {
            POLogger.info("Starting card tokenization.")
            dispatch(WillStart)
            initAddressFields()
            collectPreferredScheme()
            POLogger.info("Card tokenization is started: waiting for user input.")
            dispatch(DidStart)
        }
    }

    private fun init() = CardTokenizationInteractorState(
        cardFields = cardFields(),
        addressFields = emptyList(),
        focusedFieldId = CardFieldId.NUMBER,
        primaryActionId = ActionId.SUBMIT,
        secondaryActionId = ActionId.CANCEL
    )

    private fun cardFields(): List<Field> = mutableListOf(
        Field(id = CardFieldId.NUMBER),
        Field(id = CardFieldId.EXPIRATION),
        Field(id = CardFieldId.CVC),
        Field(
            id = CardFieldId.CARDHOLDER,
            shouldCollect = configuration.isCardholderNameFieldVisible
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

        val availableValues = supportedCountryCodes.map {
            POAvailableValue(
                value = it,
                text = Locale(String(), it).displayCountry
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

    fun onEvent(event: CardTokenizationEvent) {
        when (event) {
            is FieldValueChanged -> updateFieldValue(event.id, event.value)
            is FieldFocusChanged -> updateFieldFocus(event.id, event.isFocused)
            is Action -> when (event.id) {
                ActionId.SUBMIT -> submit()
                ActionId.CANCEL -> cancel()
            }
            is Dismiss -> POLogger.info("Dismissed: %s", event.failure)
        }
    }

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
                }
            )
        }
        if (isTextChanged) {
            POLogger.debug(message = "Field is edited by the user: %s", id)
            dispatch(ParametersChanged)
            if (areAllFieldsValid()) {
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
        if (id == field.id) {
            if (isTextChanged) {
                field.copy(value = value, isValid = true)
            } else {
                field.copy(value = value)
            }
        } else {
            field.copy()
        }

    private fun updateFieldFocus(id: String, isFocused: Boolean) {
        if (isFocused) {
            _state.update { it.copy(focusedFieldId = id) }
        }
    }

    //region Issuer Information & Preferred Scheme

    private fun updateIssuerInformation(cardNumber: String, previousCardNumber: String) {
        val iin = iin(cardNumber)
        if (iin == iin(previousCardNumber)) {
            return
        }
        updateState(
            issuerInformation = localIssuerInformation(iin),
            preferredScheme = null
        )
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
                if (eventDispatcher.subscribedForPreferredSchemeRequest()) {
                    requestPreferredScheme(issuerInformation)
                } else {
                    updateState(
                        issuerInformation = issuerInformation,
                        preferredScheme = null
                    )
                }
            }
        }
    }

    private suspend fun fetchIssuerInformation(iin: String) =
        cardsRepository.fetchIssuerInformation(iin)
            .onFailure {
                POLogger.info(
                    message = "Failed to fetch issuer information: %s", it,
                    attributes = mapOf(LOG_ATTRIBUTE_IIN to iin)
                )
            }.getOrNull()

    private suspend fun requestPreferredScheme(issuerInformation: POCardIssuerInformation) {
        val request = POCardTokenizationPreferredSchemeRequest(issuerInformation)
        latestPreferredSchemeRequest = request
        eventDispatcher.send(request)
        POLogger.info("Requested to choose preferred scheme by issuer information: %s", issuerInformation)
    }

    private fun collectPreferredScheme() {
        interactorScope.launch {
            eventDispatcher.preferredSchemeResponse.collect { response ->
                if (response.uuid == latestPreferredSchemeRequest?.uuid) {
                    latestPreferredSchemeRequest = null
                    updateState(
                        issuerInformation = response.issuerInformation,
                        preferredScheme = response.preferredScheme
                    )
                }
            }
        }
    }

    private fun updateState(
        issuerInformation: POCardIssuerInformation?,
        preferredScheme: String?
    ) {
        _state.update {
            it.copy(
                issuerInformation = issuerInformation,
                preferredScheme = preferredScheme
            )
        }
        POLogger.info("State updated: [issuerInformation=%s] [preferredScheme=%s]", issuerInformation, preferredScheme)
    }

    //endregion

    //region Address Specification

    private fun updateAddressSpecification() {
        _state.value.addressFields.find { it.id == AddressFieldId.COUNTRY }?.let { countryField ->
            interactorScope.launch {
                val countryCode = countryField.value.text
                val specification = addressSpecificationProvider.specification(countryCode)
                val addressFields = mutableListOf(countryField.copy())
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
        specification.units?.forEach { unit ->
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
                else -> true
            }
            Full -> true
        }

    //endregion

    private fun submit() {
        if (!areAllFieldsValid()) {
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
        tokenize(tokenizationRequest())
    }

    private fun allFields(): List<Field> = with(_state.value) { cardFields + addressFields }

    private fun areAllFieldsValid(): Boolean = allFields().all { it.isValid }

    private fun tokenize(request: POCardTokenizationRequest) {
        // TODO
    }

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
            preferredScheme = _state.value.preferredScheme,
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

    private fun cancel() {
        _completion.update {
            CardTokenizationCompletion.Failure(
                ProcessOutResult.Failure(
                    code = POFailure.Code.Cancelled,
                    message = "Cancelled by the user with secondary cancel action."
                ).also { POLogger.info("Cancelled: %s", it) }
            )
        }
    }

    private fun dispatch(event: POCardTokenizationEvent) {
        interactorScope.launch {
            eventDispatcher.send(event)
        }
    }
}
