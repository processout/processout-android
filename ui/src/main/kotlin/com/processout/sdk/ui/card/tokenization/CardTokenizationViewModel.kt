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
import com.processout.sdk.api.model.event.POCardTokenizationEvent.DidStart
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
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration.RestoreConfiguration
import com.processout.sdk.ui.card.tokenization.POCardTokenizationFormData.CardInformation
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POMutableFieldState
import com.processout.sdk.ui.core.state.POStableList
import com.processout.sdk.ui.shared.filter.CardExpirationInputFilter
import com.processout.sdk.ui.shared.filter.CardNumberInputFilter
import com.processout.sdk.ui.shared.filter.CardSecurityCodeInputFilter
import com.processout.sdk.ui.shared.provider.CardSchemeProvider
import com.processout.sdk.ui.shared.provider.cardSchemeDrawableResId
import com.processout.sdk.ui.shared.transformation.CardExpirationVisualTransformation
import com.processout.sdk.ui.shared.transformation.CardNumberVisualTransformation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class CardTokenizationViewModel(
    private val app: Application,
    private val configuration: POCardTokenizationConfiguration,
    private val cardsRepository: POCardsRepository,
    private val cardSchemeProvider: CardSchemeProvider,
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
                eventDispatcher = PODefaultCardTokenizationEventDispatcher
            ) as T
    }

    private companion object {
        const val LOG_ATTRIBUTE_IIN = "IIN"
        const val EXPIRATION_DATE_PART_LENGTH = 2
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

    private val preferredSchemeRequests = mutableSetOf<POCardTokenizationPreferredSchemeRequest>()
    private val shouldContinueRequests = mutableSetOf<POCardTokenizationShouldContinueRequest>()

    init {
        setLastFieldImeAction()
        collectPreferredScheme()
        shouldContinueOnFailure()
        POLogger.info("Card tokenization is started: waiting for user input.")
        dispatch(DidStart)
        configuration.restore?.let { restore(it) }
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

    private fun cardNumberField(): Item.TextField {
        val number = configuration.restore?.formData?.cardInformation?.number ?: String()
        return Item.TextField(
            POMutableFieldState(
                id = CardFieldId.NUMBER,
                value = TextFieldValue(
                    text = number,
                    selection = TextRange(number.length)
                ),
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
        val cvc = configuration.restore?.formData?.cardInformation?.cvc ?: String()
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
                inputFilter = CardSecurityCodeInputFilter(scheme = null),
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

    private fun setLastFieldImeAction() {
        lastField()?.apply {
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
            val isTextChanged = it.value.text != value.text
            it.value = value
            if (isTextChanged) {
                it.isError = false
                if (areAllFieldsValid()) {
                    updateState(
                        submitAllowed = true,
                        submitting = _state.value.submitting,
                        errorMessage = null
                    )
                }
                if (id == CardFieldId.NUMBER) {
                    updateIssuerInformation(cardNumber = value.text)
                }
            }
        }
    }

    private fun updateFieldFocus(id: String, isFocused: Boolean) {
        if (isFocused) {
            _state.update { it.copy(focusedFieldId = id) }
        }
    }

    private fun updateIssuerInformation(cardNumber: String) {
        when (cardNumber.length) {
            in 0..6 -> localIssuerInformation(cardNumber).let { issuerInformation ->
                _state.update { it.copy(issuerInformation = issuerInformation) }
                updateFields(issuerInformation)
            }
        }
        when (cardNumber.length) {
            6, 8 -> viewModelScope.launch {
                fetchIssuerInformation(cardNumber)?.let { issuerInformation ->
                    _state.update { it.copy(issuerInformation = issuerInformation) }
                    updateFields(issuerInformation)
                }
            }
        }
    }

    private fun localIssuerInformation(cardNumber: String) =
        cardSchemeProvider.scheme(cardNumber)?.let { scheme ->
            POCardIssuerInformation(scheme = scheme)
        }

    private suspend fun fetchIssuerInformation(iin: String) =
        cardsRepository.fetchIssuerInformation(iin)
            .onFailure {
                POLogger.info(
                    message = "Failed to fetch issuer information: %s", it,
                    attributes = mapOf(LOG_ATTRIBUTE_IIN to iin)
                )
            }.getOrNull()

    private fun updateFields(issuerInformation: POCardIssuerInformation?) {
        val scheme = issuerInformation?.coScheme ?: issuerInformation?.scheme
        field(CardFieldId.NUMBER)?.apply {
            iconResId = scheme?.let { cardSchemeDrawableResId(it) }
        }
        field(CardFieldId.CVC)?.apply {
            val inputFilter = CardSecurityCodeInputFilter(scheme = scheme)
            value = inputFilter.filter(value)
            this.inputFilter = inputFilter
        }
    }

    private fun requestPreferredScheme(issuerInformation: POCardIssuerInformation) {
        viewModelScope.launch {
            val request = POCardTokenizationPreferredSchemeRequest(issuerInformation)
            preferredSchemeRequests.add(request)
            eventDispatcher.send(request)
        }
    }

    private fun collectPreferredScheme() {
        viewModelScope.launch {
            eventDispatcher.preferredSchemeResponse.collect { response ->
                if (preferredSchemeRequests.removeAll { it.uuid == response.uuid }) {
                    // TODO
                }
            }
        }
    }

    private fun submit() {
        if (!areAllFieldsValid()) {
            POLogger.debug("Ignored attempt to tokenize with invalid values.")
            return
        }
        updateState(
            submitAllowed = true,
            submitting = true,
            errorMessage = null
        )
        tokenize(fieldValues().toFormData())
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
        viewModelScope.launch {
            cardsRepository.tokenize(formData.toRequest())
                .onSuccess { card ->
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
            shouldContinueRequests.add(request)
            eventDispatcher.send(request)
            POLogger.info("Requested to decide whether the flow should continue or complete after the failure: %s", failure)
        }
    }

    private fun shouldContinueOnFailure() {
        viewModelScope.launch {
            eventDispatcher.shouldContinueResponse.collect { response ->
                if (shouldContinueRequests.removeAll { it.uuid == response.uuid }) {
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
            is Item.Group -> item.items.elements.forEach { groupItem ->
                field(id, groupItem)
                    ?.let { return it }
            }
        }
        return null
    }

    private fun lastField(): POMutableFieldState? {
        _sections.lastOrNull()?.let { section ->
            section.items.elements.lastOrNull()?.let { item ->
                return lastField(item)
            }
        }
        return null
    }

    private fun lastField(item: Item): POMutableFieldState? {
        when (item) {
            is Item.TextField -> return item.state
            is Item.Group -> item.items.elements.lastOrNull()?.let { groupItem ->
                return lastField(groupItem)
            }
        }
        return null
    }

    private fun fieldValues(): List<FieldValue> {
        val fieldValues = mutableListOf<FieldValue>()
        _sections.forEach { section ->
            section.items.elements.forEach { item ->
                fieldValues(item, fieldValues)
            }
        }
        return fieldValues
    }

    private fun fieldValues(item: Item, fieldValues: MutableList<FieldValue>) {
        when (item) {
            is Item.TextField -> with(item.state) {
                fieldValues.add(
                    FieldValue(
                        id = id,
                        value = value.text,
                        isValid = !isError
                    )
                )
            }
            is Item.Group -> item.items.elements.forEach { groupItem ->
                fieldValues(groupItem, fieldValues)
            }
        }
    }

    private fun List<FieldValue>.toFormData(): POCardTokenizationFormData {
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
                cardholderName = cardholderName
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
                preferredScheme = _state.value.preferredScheme,
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
