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
import com.processout.sdk.api.model.response.POCardIssuerInformation
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.core.*
import com.processout.sdk.core.POFailure.GenericCode.*
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.card.tokenization.CardTokenizationCompletion.*
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent.*
import com.processout.sdk.ui.card.tokenization.CardTokenizationSection.Item
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration.CollectionMode
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration.RestoreConfiguration
import com.processout.sdk.ui.card.tokenization.POCardTokenizationFormData.CardInformation
import com.processout.sdk.ui.core.state.*
import com.processout.sdk.ui.shared.extension.currentAppLocale
import com.processout.sdk.ui.shared.extension.orElse
import com.processout.sdk.ui.shared.filter.CardExpirationInputFilter
import com.processout.sdk.ui.shared.filter.CardNumberInputFilter
import com.processout.sdk.ui.shared.filter.CardSecurityCodeInputFilter
import com.processout.sdk.ui.shared.provider.CardSchemeProvider
import com.processout.sdk.ui.shared.provider.address.AddressSpecificationProvider
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

    init {
        viewModelScope.launch {
            if (configuration.billingAddress.mode != CollectionMode.Never) {
                val countryCodes = addressSpecificationProvider.countryCodes()
                billingAddressSection(countryCodes)?.let { _sections.add(it) }
            }
            setLastFieldImeAction()
            collectPreferredScheme()
            shouldContinueOnFailure()
            configuration.restore?.let {
                POLogger.info("Restoring card tokenization.")
                restore(it)
                dispatch(DidStart(restored = true))
            }.orElse {
                POLogger.info("Card tokenization is started: waiting for user input.")
                dispatch(DidStart(restored = false))
            }
        }
    }

    private fun initState() = with(configuration) {
        val cardInformation = restore?.formData?.cardInformation
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
            issuerInformation = cardInformation?.issuerInformation,
            preferredScheme = cardInformation?.preferredScheme,
            focusedFieldId = CardFieldId.NUMBER,
            draggable = cancellation.dragDown
        )
    }

    private fun cardInformationSection(): CardTokenizationSection {
        val items = mutableListOf(
            cardNumberField(),
            Item.Group(
                items = POStableList(
                    listOf(
                        cardExpirationField(),
                        cvcField()
                    )
                )
            )
        )
        if (configuration.isCardholderNameFieldVisible) {
            items.add(cardholderField())
        }
        return CardTokenizationSection(
            id = SectionId.CARD_INFORMATION,
            items = POStableList(items)
        )
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
            items = POStableList(items)
        )
    }

    private fun cardNumberField(): Item.TextField {
        val cardInformation = configuration.restore?.formData?.cardInformation
        val number = cardInformation?.number ?: String()
        val scheme = cardInformation?.preferredScheme ?: cardInformation?.issuerInformation?.scheme
        return Item.TextField(
            POMutableFieldState(
                id = CardFieldId.NUMBER,
                value = TextFieldValue(
                    text = number,
                    selection = TextRange(number.length)
                ),
                placeholder = app.getString(R.string.po_card_tokenization_card_details_number_placeholder),
                forceTextDirectionLtr = true,
                iconResId = scheme?.let { cardSchemeDrawableResId(it) },
                inputFilter = CardNumberInputFilter(),
                visualTransformation = CardNumberVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )
        )
    }

    private fun cardExpirationField(): Item.TextField {
        val expiration = configuration.restore?.formData?.cardInformation?.expiration ?: String()
        return Item.TextField(
            POMutableFieldState(
                id = CardFieldId.EXPIRATION,
                value = TextFieldValue(
                    text = expiration,
                    selection = TextRange(expiration.length)
                ),
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
    }

    private fun cvcField(): Item.TextField {
        val cardInformation = configuration.restore?.formData?.cardInformation
        val cvc = cardInformation?.cvc ?: String()
        return Item.TextField(
            POMutableFieldState(
                id = CardFieldId.CVC,
                value = TextFieldValue(
                    text = cvc,
                    selection = TextRange(cvc.length)
                ),
                placeholder = app.getString(R.string.po_card_tokenization_card_details_cvc_placeholder),
                forceTextDirectionLtr = true,
                iconResId = com.processout.sdk.ui.R.drawable.po_card_back,
                inputFilter = CardSecurityCodeInputFilter(
                    scheme = cardInformation?.issuerInformation?.scheme
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Next
                )
            )
        )
    }

    private fun cardholderField(): Item.TextField {
        val cardholderName = configuration.restore?.formData?.cardInformation?.cardholderName ?: String()
        return Item.TextField(
            POMutableFieldState(
                id = CardFieldId.CARDHOLDER,
                value = TextFieldValue(
                    text = cardholderName,
                    selection = TextRange(cardholderName.length)
                ),
                placeholder = app.getString(R.string.po_card_tokenization_card_details_cardholder_placeholder),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )
        )
    }

    private fun countryField(countryCodes: Set<String>): Item.DropdownField? {
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

    private fun setLastFieldImeAction() {
        lastFocusableField()?.apply {
            keyboardOptions = keyboardOptions.copy(imeAction = ImeAction.Done)
            keyboardActionId = ActionId.SUBMIT
        }
    }

    private fun restore(configuration: RestoreConfiguration) {
        val failureCode = configuration.failureCode ?: POFailure.Code.Generic()
        handle(ProcessOutResult.Failure(failureCode))
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
            if (id == CardFieldId.NUMBER) {
                updateIssuerInformation(
                    cardNumber = value.text,
                    oldCardNumber = oldText
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

    private fun tokenize(formData: POCardTokenizationFormData) {
        POLogger.info(message = "Submitting card information.")
        dispatch(WillTokenizeCard)
        viewModelScope.launch {
            cardsRepository.tokenize(formData.toRequest())
                .onSuccess { card ->
                    POLogger.info(message = "Card tokenized successfully.")
                    dispatch(DidComplete)
                    _completion.update {
                        Success(
                            POCardTokenizationData(
                                card = card,
                                formData = formData
                            )
                        )
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

    private fun field(id: String): POMutableFieldState? {
        _sections.forEach { section ->
            section.items.elements.forEach { item ->
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
            is Item.Group -> item.items.elements.forEach { groupItem ->
                field(id, groupItem)
                    ?.let { return it }
            }
        }
        return null
    }

    private fun lastFocusableField(): POMutableFieldState? {
        _sections.reversed().forEach { section ->
            section.items.elements.reversed().forEach { item ->
                lastFocusableField(item)
                    ?.let { return it }
            }
        }
        return null
    }

    private fun lastFocusableField(item: Item): POMutableFieldState? {
        when (item) {
            is Item.TextField -> return item.state
            is Item.Group -> item.items.elements.reversed().forEach { groupItem ->
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
            section.items.elements.forEach { item ->
                addFieldValues(item, fieldValues)
            }
        }
        return fieldValues
    }

    private fun addFieldValues(item: Item, fieldValues: MutableList<FieldValue>) {
        when (item) {
            is Item.TextField -> addFieldValue(item.state, fieldValues)
            is Item.DropdownField -> addFieldValue(item.state, fieldValues)
            is Item.Group -> item.items.elements.forEach { groupItem ->
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
        state: CardTokenizationState
    ): POCardTokenizationFormData {
        var number = String()
        var expiration = String()
        var cvc = String()
        var cardholderName = String()
        forEach {
            when (it.id) {
                CardFieldId.NUMBER -> number = it.value
                CardFieldId.EXPIRATION -> expiration = it.value
                CardFieldId.CVC -> cvc = it.value
                CardFieldId.CARDHOLDER -> cardholderName = it.value
            }
        }
        return POCardTokenizationFormData(
            cardInformation = CardInformation(
                number = number,
                expiration = expiration,
                cvc = cvc,
                cardholderName = cardholderName,
                issuerInformation = state.issuerInformation,
                preferredScheme = state.preferredScheme
            )
        )
    }

    private fun POCardTokenizationFormData.toRequest(): POCardTokenizationRequest {
        with(cardInformation) {
            val expiration = expiration(expiration)
            return POCardTokenizationRequest(
                number = number,
                expMonth = expiration.month,
                expYear = expiration.year,
                cvc = cvc,
                name = cardholderName,
                preferredScheme = preferredScheme,
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
}
