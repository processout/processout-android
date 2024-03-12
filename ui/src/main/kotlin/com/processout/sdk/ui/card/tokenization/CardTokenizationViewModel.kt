package com.processout.sdk.ui.card.tokenization

import android.app.Application
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.dispatcher.card.tokenization.PODefaultCardTokenizationEventDispatcher
import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.event.POCardTokenizationEvent.*
import com.processout.sdk.api.model.request.POCardTokenizationPreferredSchemeRequest
import com.processout.sdk.api.model.request.POCardTokenizationRequest
import com.processout.sdk.api.model.request.POCardTokenizationShouldContinueRequest
import com.processout.sdk.api.model.request.POContact
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.api.model.response.POCardIssuerInformation
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.core.*
import com.processout.sdk.core.POFailure.GenericCode.*
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.card.tokenization.CardTokenizationCompletion.*
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent.*
import com.processout.sdk.ui.card.tokenization.CardTokenizationFormData.BillingAddress
import com.processout.sdk.ui.card.tokenization.CardTokenizationFormData.CardInformation
import com.processout.sdk.ui.card.tokenization.CardTokenizationSection.Item
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration.CollectionMode.*
import com.processout.sdk.ui.core.state.*
import com.processout.sdk.ui.shared.extension.currentAppLocale
import com.processout.sdk.ui.shared.extension.orElse
import com.processout.sdk.ui.shared.filter.CardExpirationInputFilter
import com.processout.sdk.ui.shared.filter.CardNumberInputFilter
import com.processout.sdk.ui.shared.filter.CardSecurityCodeInputFilter
import com.processout.sdk.ui.shared.provider.CardSchemeProvider
import com.processout.sdk.ui.shared.provider.address.AddressSpecification
import com.processout.sdk.ui.shared.provider.address.AddressSpecification.AddressUnit
import com.processout.sdk.ui.shared.provider.address.AddressSpecificationProvider
import com.processout.sdk.ui.shared.provider.address.stringResId
import com.processout.sdk.ui.shared.provider.cardSchemeDrawableResId
import com.processout.sdk.ui.shared.transformation.CardExpirationVisualTransformation
import com.processout.sdk.ui.shared.transformation.CardNumberVisualTransformation
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

internal class CardTokenizationViewModel(
    private val app: Application,
    private val configuration: POCardTokenizationConfiguration,
    private val cardsRepository: POCardsRepository,
    private val cardSchemeProvider: CardSchemeProvider,
    private val addressSpecificationProvider: AddressSpecificationProvider,
    private val eventDispatcher: PODefaultCardTokenizationEventDispatcher
) : ViewModel() {

    class Factory(
        private val app: Application,
        private val configuration: POCardTokenizationConfiguration
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CardTokenizationViewModel(
                app = app,
                configuration = configuration,
                cardsRepository = ProcessOut.instance.cards,
                cardSchemeProvider = CardSchemeProvider(),
                addressSpecificationProvider = AddressSpecificationProvider(app),
                eventDispatcher = PODefaultCardTokenizationEventDispatcher
            ) as T
    }

    private companion object {
        const val IIN_LENGTH = 6
        const val EXPIRATION_DATE_PART_LENGTH = 2
        const val LOG_ATTRIBUTE_IIN = "IIN"
        const val LOG_ATTRIBUTE_CARD_ID = "CardId"
    }

    private object SectionId {
        const val CARD_INFORMATION = "card-information"
        const val BILLING_ADDRESS = "billing-address"
    }

    private object CardFieldId {
        const val NUMBER = "card-number"
        const val EXPIRATION = "card-expiration"
        const val CVC = "card-cvc"
        const val CARDHOLDER = "cardholder-name"
    }

    private object AddressFieldId {
        const val COUNTRY = "country-code"
        const val ADDRESS_1 = "address-1"
        const val ADDRESS_2 = "address-2"
        const val CITY = "city"
        const val STATE = "state"
        const val POSTAL_CODE = "postal-code"
    }

    private object ActionId {
        const val SUBMIT = "submit"
        const val CANCEL = "cancel"
    }

    private data class FieldValue(
        val id: String,
        val value: String,
        val isValid: Boolean
    )

    private data class Expiration(
        val month: Int,
        val year: Int
    )

    private val _completion = MutableStateFlow<CardTokenizationCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    private val _sections = mutableStateListOf(cardInformationSection())
    val sections = POStableList(_sections)

    private var latestPreferredSchemeRequest: POCardTokenizationPreferredSchemeRequest? = null
    private var latestShouldContinueRequest: POCardTokenizationShouldContinueRequest? = null

    private var issuerInformationJob: Job? = null
    private var addressValues: POContact? = null

    init {
        viewModelScope.launch {
            POLogger.info("Starting card tokenization.")
            dispatch(WillStart)
            initBillingAddressSection()
            updateImeActions()
            collectPreferredScheme()
            handleCompletion()
            shouldContinueOnFailure()
            POLogger.info("Card tokenization is started: waiting for user input.")
            dispatch(DidStart)
        }
    }

    private fun initState() = with(configuration) {
        CardTokenizationState(
            title = title ?: app.getString(R.string.po_card_tokenization_title),
            primaryAction = POActionState(
                id = ActionId.SUBMIT,
                text = primaryActionText ?: app.getString(R.string.po_card_tokenization_button_submit),
                primary = true
            ),
            secondaryAction = if (cancellation.secondaryAction) POActionState(
                id = ActionId.CANCEL,
                text = secondaryActionText ?: app.getString(R.string.po_card_tokenization_button_cancel),
                primary = false
            ) else null,
            focusedFieldId = CardFieldId.NUMBER,
            draggable = cancellation.dragDown
        )
    }

    private fun cardInformationSection(): CardTokenizationSection {
        val items = mutableListOf(
            cardNumberField(),
            Item.Group(listOf(cardExpirationField(), cvcField()))
        )
        if (configuration.isCardholderNameFieldVisible) {
            items.add(cardholderField())
        }
        return CardTokenizationSection(
            id = SectionId.CARD_INFORMATION,
            items = items
        )
    }

    private suspend fun initBillingAddressSection() {
        if (configuration.billingAddress.mode == Never) {
            return
        }
        val countryCodes = addressSpecificationProvider.countryCodes()
        billingAddressSection(countryCodes)?.let {
            _sections.add(it)
            field(AddressFieldId.COUNTRY)?.let { field ->
                updateAddressSpecification(countryCode = field.value.text)
            }
        }
    }

    private fun billingAddressSection(countryCodes: Set<String>): CardTokenizationSection? {
        val items = mutableListOf<Item>()
        countryField(countryCodes)?.let { items.add(it) }
        if (items.isEmpty()) {
            return null
        }
        return CardTokenizationSection(
            id = SectionId.BILLING_ADDRESS,
            title = app.getString(R.string.po_card_tokenization_billing_address_title),
            items = items
        )
    }

    private fun updateAddressSpecification(countryCode: String) {
        _sections.find { it.id == SectionId.BILLING_ADDRESS }?.apply {
            viewModelScope.launch {
                val specification = addressSpecificationProvider.specification(countryCode)
                val addressFields = addressFields(countryCode, specification)
                clearItems(keepIds = setOf(AddressFieldId.COUNTRY))
                items.addAll(addressFields)
                updateImeActions()
            }
        }
    }

    private fun cardNumberField(): Item = Item.TextField(
        POMutableFieldState(
            id = CardFieldId.NUMBER,
            placeholder = app.getString(R.string.po_card_tokenization_card_details_number_placeholder),
            forceTextDirectionLtr = true,
            inputFilter = CardNumberInputFilter(),
            visualTransformation = CardNumberVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )
    )

    private fun cardExpirationField(): Item = Item.TextField(
        POMutableFieldState(
            id = CardFieldId.EXPIRATION,
            placeholder = app.getString(R.string.po_card_tokenization_card_details_expiration_placeholder),
            forceTextDirectionLtr = true,
            inputFilter = CardExpirationInputFilter(),
            visualTransformation = CardExpirationVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )
    )

    private fun cvcField(): Item = Item.TextField(
        POMutableFieldState(
            id = CardFieldId.CVC,
            placeholder = app.getString(R.string.po_card_tokenization_card_details_cvc_placeholder),
            forceTextDirectionLtr = true,
            iconResId = com.processout.sdk.ui.R.drawable.po_card_back,
            inputFilter = CardSecurityCodeInputFilter(scheme = null),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            )
        )
    )

    private fun cardholderField(): Item = Item.TextField(
        POMutableFieldState(
            id = CardFieldId.CARDHOLDER,
            placeholder = app.getString(R.string.po_card_tokenization_card_details_cardholder_placeholder),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                autoCorrect = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )
    )

    private fun countryField(countryCodes: Set<String>): Item? {
        val supportedCountryCodes = configuration.billingAddress.countryCodes
            ?.let { configurationCountryCodes ->
                configurationCountryCodes.filter { countryCodes.contains(it) }
            }.orElse { countryCodes }
        if (supportedCountryCodes.isEmpty()) {
            return null
        }

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
        return Item.DropdownField(
            POMutableFieldState(
                id = AddressFieldId.COUNTRY,
                value = TextFieldValue(text = defaultCountryCode),
                availableValues = POImmutableList(availableValues)
            )
        )
    }

    private fun addressFields(countryCode: String, specification: AddressSpecification): List<Item> {
        val defaultAddress = configuration.billingAddress.defaultAddress
        val address1 = addressValues?.address1 ?: defaultAddress?.address1 ?: String()
        val address2 = addressValues?.address2 ?: defaultAddress?.address2 ?: String()
        val city = addressValues?.city ?: defaultAddress?.city ?: String()
        val state = addressValues?.state ?: defaultAddress?.state ?: String()
        val postalCode = addressValues?.zip ?: defaultAddress?.zip ?: String()
        addressValues = POContact(
            address1 = address1,
            address2 = address2,
            city = city,
            state = state,
            zip = postalCode
        )
        val fields = mutableListOf<Item>()
        specification.units?.forEach { unit ->
            when (unit) {
                AddressUnit.street -> {
                    val streetFields = listOf(
                        Item.TextField(
                            POMutableFieldState(
                                id = AddressFieldId.ADDRESS_1,
                                value = TextFieldValue(
                                    text = address1,
                                    selection = TextRange(address1.length)
                                ),
                                placeholder = app.getString(R.string.po_card_tokenization_billing_address_street, 1),
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                )
                            )
                        ),
                        Item.TextField(
                            POMutableFieldState(
                                id = AddressFieldId.ADDRESS_2,
                                value = TextFieldValue(
                                    text = address2,
                                    selection = TextRange(address2.length)
                                ),
                                placeholder = app.getString(R.string.po_card_tokenization_billing_address_street, 2),
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                )
                            )
                        )
                    )
                    fields.addAll(streetFields)
                }
                AddressUnit.city -> Item.TextField(
                    POMutableFieldState(
                        id = AddressFieldId.CITY,
                        value = TextFieldValue(
                            text = city,
                            selection = TextRange(city.length)
                        ),
                        placeholder = specification.cityUnit?.let { app.getString(it.stringResId()) },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )
                ).also { fields.add(it) }
                AddressUnit.state -> Item.TextField(
                    POMutableFieldState(
                        id = AddressFieldId.STATE,
                        value = TextFieldValue(
                            text = state,
                            selection = TextRange(state.length)
                        ),
                        placeholder = specification.stateUnit?.let { app.getString(it.stringResId()) },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )
                ).also { fields.add(it) }
                AddressUnit.postcode -> {
                    val supportedCountryCodes = setOf("US", "GB", "CA")
                    val collectionMode = configuration.billingAddress.mode
                    if (collectionMode == Full ||
                        collectionMode == Automatic && supportedCountryCodes.contains(countryCode)
                    ) {
                        Item.TextField(
                            POMutableFieldState(
                                id = AddressFieldId.POSTAL_CODE,
                                value = TextFieldValue(
                                    text = postalCode,
                                    selection = TextRange(postalCode.length)
                                ),
                                placeholder = specification.postcodeUnit?.let { app.getString(it.stringResId()) },
                                forceTextDirectionLtr = true,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Characters,
                                    autoCorrect = false,
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                )
                            )
                        ).also { fields.add(it) }
                    }
                }
            }
        }
        return fields
    }

    private fun updateImeActions() {
        resetImeActions()
        lastFocusableField()?.let {
            updateImeAction(
                fieldState = it,
                imeAction = ImeAction.Done
            )
        }
    }

    private fun resetImeActions() {
        _sections.forEach { section ->
            section.items.forEach { item ->
                resetImeActions(item)
            }
        }
    }

    private fun resetImeActions(item: Item) {
        when (item) {
            is Item.TextField -> updateImeAction(
                fieldState = item.state,
                imeAction = ImeAction.Next
            )
            is Item.Group -> item.items.forEach { groupItem ->
                resetImeActions(groupItem)
            }
            else -> {}
        }
    }

    private fun updateImeAction(fieldState: POMutableFieldState, imeAction: ImeAction) {
        with(fieldState) {
            keyboardOptions = keyboardOptions.copy(imeAction = imeAction)
            keyboardActionId = when (imeAction) {
                ImeAction.Done -> ActionId.SUBMIT
                else -> null
            }
        }
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
        field(id)?.let {
            val oldText = it.value.text
            it.value = value
            if (value.text == oldText) {
                return
            }
            it.isError = false
            POLogger.debug(message = "Field is edited by the user: %s", id)
            dispatch(ParametersChanged)
            if (areAllFieldsValid()) {
                updateState(
                    submitAllowed = true,
                    submitting = _state.value.submitting,
                    errorMessage = null
                )
            }
            when (id) {
                CardFieldId.NUMBER -> updateIssuerInformation(
                    cardNumber = value.text,
                    oldCardNumber = oldText
                )
                AddressFieldId.COUNTRY -> updateAddressSpecification(
                    countryCode = value.text
                )
                AddressFieldId.ADDRESS_1 -> addressValues = addressValues?.copy(
                    address1 = value.text
                )
                AddressFieldId.ADDRESS_2 -> addressValues = addressValues?.copy(
                    address2 = value.text
                )
                AddressFieldId.CITY -> addressValues = addressValues?.copy(
                    city = value.text
                )
                AddressFieldId.STATE -> addressValues = addressValues?.copy(
                    state = value.text
                )
                AddressFieldId.POSTAL_CODE -> addressValues = addressValues?.copy(
                    zip = value.text
                )
            }
        }
    }

    private fun updateFieldFocus(id: String, isFocused: Boolean) {
        if (isFocused) {
            _state.update { it.copy(focusedFieldId = id) }
        }
    }

    private fun updateIssuerInformation(cardNumber: String, oldCardNumber: String) {
        val iin = iin(cardNumber)
        if (iin == iin(oldCardNumber)) {
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
        issuerInformationJob = viewModelScope.launch {
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
        viewModelScope.launch {
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
        val scheme = preferredScheme ?: issuerInformation?.scheme
        field(CardFieldId.NUMBER)?.apply {
            iconResId = scheme?.let { cardSchemeDrawableResId(it) }
        }
        field(CardFieldId.CVC)?.apply {
            val inputFilter = CardSecurityCodeInputFilter(scheme = issuerInformation?.scheme)
            value = inputFilter.filter(value)
            this.inputFilter = inputFilter
        }
        POLogger.info("State updated: [issuerInformation=%s] [preferredScheme=%s]", issuerInformation, preferredScheme)
    }

    private fun submit() {
        if (!areAllFieldsValid()) {
            POLogger.debug("Ignored attempt to tokenize the card with invalid values.")
            return
        }
        updateState(
            submitAllowed = true,
            submitting = true,
            errorMessage = null
        )
        tokenize(fieldValues().toFormData(_state.value))
    }

    private fun areAllFieldsValid() = fieldValues().all { it.isValid }

    private fun updateState(
        submitAllowed: Boolean,
        submitting: Boolean,
        errorMessage: String?
    ) {
        _state.update {
            with(it) {
                copy(
                    primaryAction = primaryAction.copy(
                        enabled = submitAllowed,
                        loading = submitting
                    ),
                    secondaryAction = secondaryAction?.copy(
                        enabled = !submitting
                    ),
                    submitting = submitting
                )
            }
        }
        updateErrorMessage(errorMessage)
    }

    private fun updateErrorMessage(value: String?) {
        _sections.find { it.id == SectionId.CARD_INFORMATION }?.apply {
            errorMessage = value
        }
    }

    private fun tokenize(formData: CardTokenizationFormData) {
        POLogger.info(message = "Submitting card information.")
        dispatch(WillTokenize)
        viewModelScope.launch {
            cardsRepository.tokenize(formData.toRequest())
                .onSuccess { card ->
                    _state.update { it.copy(tokenizedCard = card) }
                    POLogger.info(
                        message = "Card tokenized successfully.",
                        attributes = mapOf(LOG_ATTRIBUTE_CARD_ID to card.id)
                    )
                    dispatch(DidTokenize(card))
                    if (eventDispatcher.subscribedForProcessTokenizedCard()) {
                        requestToProcessTokenizedCard(card)
                    } else {
                        POLogger.info(
                            message = "Completed successfully.",
                            attributes = mapOf(LOG_ATTRIBUTE_CARD_ID to card.id)
                        )
                        _completion.update { Success(card) }
                    }
                }.onFailure { failure ->
                    if (eventDispatcher.subscribedForShouldContinueRequest()) {
                        requestIfShouldContinue(failure)
                    } else {
                        handle(failure)
                    }
                }
        }
    }

    private fun requestToProcessTokenizedCard(card: POCard) {
        viewModelScope.launch {
            eventDispatcher.processTokenizedCard(card)
            POLogger.info(
                message = "Requested to process tokenized card.",
                attributes = mapOf(LOG_ATTRIBUTE_CARD_ID to card.id)
            )
        }
    }

    private fun handleCompletion() {
        viewModelScope.launch {
            eventDispatcher.completion.collect { result ->
                result.onSuccess {
                    _state.value.tokenizedCard?.let { card ->
                        POLogger.info(
                            message = "Completed successfully.",
                            attributes = mapOf(LOG_ATTRIBUTE_CARD_ID to card.id)
                        )
                        _completion.update { Success(card) }
                    }.orElse {
                        val failure = ProcessOutResult.Failure(
                            code = POFailure.Code.Generic(),
                            message = "Completion is called with Success via dispatcher before card is tokenized."
                        )
                        handleCompletion(failure)
                    }
                }.onFailure { handleCompletion(it) }
            }
        }
    }

    private fun handleCompletion(failure: ProcessOutResult.Failure) {
        if (eventDispatcher.subscribedForShouldContinueRequest()) {
            requestIfShouldContinue(failure)
        } else {
            POLogger.info("Completed after the failure: %s", failure)
            _completion.update { Failure(failure) }
        }
    }

    private fun requestIfShouldContinue(failure: ProcessOutResult.Failure) {
        viewModelScope.launch {
            val request = POCardTokenizationShouldContinueRequest(failure)
            latestShouldContinueRequest = request
            eventDispatcher.send(request)
            POLogger.info("Requested to decide whether the flow should continue or complete after the failure: %s", failure)
        }
    }

    private fun shouldContinueOnFailure() {
        viewModelScope.launch {
            eventDispatcher.shouldContinueResponse.collect { response ->
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
        }
    }

    private fun handle(failure: ProcessOutResult.Failure) {
        val invalidFieldIds = mutableSetOf<String>()
        val errorMessage = when (val code = failure.code) {
            is POFailure.Code.Generic -> when (code.genericCode) {
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
                    invalidFieldIds.addAll(listOf(CardFieldId.EXPIRATION, CardFieldId.CVC))
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
            else -> app.getString(R.string.po_card_tokenization_error_generic)
        }
        invalidFieldIds.forEach { id ->
            field(id)?.apply {
                isError = true
                value = value.copy(selection = TextRange(value.text.length))
            }
        }
        fieldValues().find { !it.isValid }?.let { firstInvalidField ->
            _state.update { it.copy(focusedFieldId = firstInvalidField.id) }
        }
        updateState(
            submitAllowed = areAllFieldsValid(),
            submitting = false,
            errorMessage = errorMessage
        )
        POLogger.info(message = "Recovered after the failure: %s", failure)
    }

    private fun cancel() {
        _completion.update {
            Failure(
                ProcessOutResult.Failure(
                    code = POFailure.Code.Cancelled,
                    message = "Cancelled by the user with secondary cancel action."
                ).also { POLogger.info("Cancelled: %s", it) }
            )
        }
    }

    private fun dispatch(event: POCardTokenizationEvent) {
        viewModelScope.launch {
            eventDispatcher.send(event)
        }
    }

    private fun CardTokenizationSection.clearItems(
        keepIds: Set<String> = emptySet()
    ) {
        val iterator = items.iterator()
        while (iterator.hasNext()) {
            clearItems(iterator, keepIds)
        }
    }

    private fun CardTokenizationSection.clearItems(
        iterator: MutableIterator<Item>,
        keepIds: Set<String> = emptySet()
    ) {
        when (val item = iterator.next()) {
            is Item.TextField -> if (!keepIds.contains(item.state.id)) {
                iterator.remove()
            }
            is Item.DropdownField -> if (!keepIds.contains(item.state.id)) {
                iterator.remove()
            }
            is Item.Group -> {
                val groupIterator = item.items.iterator()
                while (groupIterator.hasNext()) {
                    clearItems(groupIterator, keepIds)
                }
            }
        }
    }

    private fun field(id: String): POMutableFieldState? {
        _sections.forEach { section ->
            section.items.forEach { item ->
                field(id, item)
                    ?.let { return it }
            }
        }
        return null
    }

    private fun field(id: String, item: Item): POMutableFieldState? {
        when (item) {
            is Item.TextField ->
                if (item.state.id == id) {
                    return item.state
                }
            is Item.DropdownField ->
                if (item.state.id == id) {
                    return item.state
                }
            is Item.Group -> item.items.forEach { groupItem ->
                field(id, groupItem)
                    ?.let { return it }
            }
        }
        return null
    }

    private fun lastFocusableField(): POMutableFieldState? {
        _sections.reversed().forEach { section ->
            section.items.reversed().forEach { item ->
                lastFocusableField(item)
                    ?.let { return it }
            }
        }
        return null
    }

    private fun lastFocusableField(item: Item): POMutableFieldState? {
        when (item) {
            is Item.TextField -> return item.state
            is Item.Group -> item.items.reversed().forEach { groupItem ->
                lastFocusableField(groupItem)
                    ?.let { return it }
            }
            else -> return null
        }
        return null
    }

    private fun fieldValues(): List<FieldValue> {
        val fieldValues = mutableListOf<FieldValue>()
        _sections.forEach { section ->
            section.items.forEach { item ->
                addFieldValues(item, fieldValues)
            }
        }
        return fieldValues
    }

    private fun addFieldValues(item: Item, fieldValues: MutableList<FieldValue>) {
        when (item) {
            is Item.TextField -> addFieldValue(item.state, fieldValues)
            is Item.DropdownField -> addFieldValue(item.state, fieldValues)
            is Item.Group -> item.items.forEach { groupItem ->
                addFieldValues(groupItem, fieldValues)
            }
        }
    }

    private fun addFieldValue(state: POMutableFieldState, fieldValues: MutableList<FieldValue>) {
        with(state) {
            fieldValues.add(
                FieldValue(
                    id = id,
                    value = value.text,
                    isValid = !isError
                )
            )
        }
    }

    private fun List<FieldValue>.toFormData(
        tokenizationState: CardTokenizationState
    ): CardTokenizationFormData {
        var cardNumber = String()
        var expiration = String()
        var cvc = String()
        var cardholderName = String()
        var countryCode = String()
        var address1 = String()
        var address2 = String()
        var city = String()
        var state = String()
        var postalCode = String()
        forEach {
            when (it.id) {
                CardFieldId.NUMBER -> cardNumber = it.value
                CardFieldId.EXPIRATION -> expiration = it.value
                CardFieldId.CVC -> cvc = it.value
                CardFieldId.CARDHOLDER -> cardholderName = it.value
                AddressFieldId.COUNTRY -> countryCode = it.value
                AddressFieldId.ADDRESS_1 -> address1 = it.value
                AddressFieldId.ADDRESS_2 -> address2 = it.value
                AddressFieldId.CITY -> city = it.value
                AddressFieldId.STATE -> state = it.value
                AddressFieldId.POSTAL_CODE -> postalCode = it.value
            }
        }
        val defaultAddress = configuration.billingAddress.defaultAddress
        countryCode = addressValue(value = countryCode, defaultValue = defaultAddress?.countryCode)
        address1 = addressValue(value = address1, defaultValue = defaultAddress?.address1)
        address2 = addressValue(value = address2, defaultValue = defaultAddress?.address2)
        city = addressValue(value = city, defaultValue = defaultAddress?.city)
        state = addressValue(value = state, defaultValue = defaultAddress?.state)
        postalCode = addressValue(value = postalCode, defaultValue = defaultAddress?.zip)
        return CardTokenizationFormData(
            cardInformation = CardInformation(
                number = cardNumber,
                expiration = expiration,
                cvc = cvc,
                cardholderName = cardholderName,
                preferredScheme = tokenizationState.preferredScheme
            ),
            billingAddress = BillingAddress(
                countryCode = countryCode,
                address1 = address1,
                address2 = address2,
                city = city,
                state = state,
                postalCode = postalCode
            )
        )
    }

    private fun addressValue(value: String, defaultValue: String?): String {
        if (!configuration.billingAddress.attachDefaultsToPaymentMethod) {
            return value
        }
        return value.ifBlank { defaultValue ?: String() }
    }

    private fun CardTokenizationFormData.toRequest(): POCardTokenizationRequest {
        with(cardInformation) {
            val expiration = expiration(expiration)
            return POCardTokenizationRequest(
                number = number,
                expMonth = expiration.month,
                expYear = expiration.year,
                cvc = cvc,
                name = cardholderName,
                preferredScheme = preferredScheme,
                contact = billingAddress.toContact(),
                metadata = configuration.metadata
            )
        }
    }

    private fun expiration(value: String): Expiration {
        val dateParts = value.chunked(EXPIRATION_DATE_PART_LENGTH)
        return Expiration(
            month = dateParts.getOrNull(0)?.toIntOrNull() ?: 0,
            year = dateParts.getOrNull(1)?.toIntOrNull() ?: 0
        )
    }

    private fun BillingAddress.toContact() = POContact(
        countryCode = countryCode,
        address1 = address1,
        address2 = address2,
        city = city,
        state = state,
        zip = postalCode
    )
}
