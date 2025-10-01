package com.processout.sdk.ui.card.update

import android.app.Application
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.event.POCardUpdateEvent
import com.processout.sdk.api.model.event.POCardUpdateEvent.*
import com.processout.sdk.api.model.request.POCardUpdateRequest
import com.processout.sdk.api.model.request.POCardUpdateShouldContinueRequest
import com.processout.sdk.api.model.response.POCardUpdateShouldContinueResponse
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.core.POFailure.Code.Cancelled
import com.processout.sdk.core.POFailure.Code.Generic
import com.processout.sdk.core.POFailure.GenericCode.*
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogAttribute
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import com.processout.sdk.ui.card.update.CardUpdateCompletion.*
import com.processout.sdk.ui.card.update.CardUpdateEvent.*
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POActionState.Confirmation
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.shared.extension.orElse
import com.processout.sdk.ui.shared.filter.CardSecurityCodeInputFilter
import com.processout.sdk.ui.shared.provider.cardSchemeDrawableResId
import com.processout.sdk.ui.shared.state.FieldState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class CardUpdateViewModel private constructor(
    private val app: Application,
    private val configuration: POCardUpdateConfiguration,
    private val cardsRepository: POCardsRepository,
    private val eventDispatcher: POEventDispatcher,
    private val logAttributes: Map<String, String>
) : ViewModel() {

    class Factory(
        private val app: Application,
        private val configuration: POCardUpdateConfiguration
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CardUpdateViewModel(
                app = app,
                configuration = configuration,
                cardsRepository = ProcessOut.instance.cards,
                eventDispatcher = POEventDispatcher.instance,
                logAttributes = mapOf(POLogAttribute.CARD_ID to configuration.cardId)
            ) as T
    }

    private object CardFieldId {
        const val NUMBER = "card-number"
        const val CVC = "card-cvc"
    }

    private object ActionId {
        const val SUBMIT = "submit"
        const val CANCEL = "cancel"
    }

    private val _completion = MutableStateFlow<CardUpdateCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    private var latestShouldContinueRequest: POCardUpdateShouldContinueRequest? = null

    init {
        collectFailure()
        shouldContinueOnFailure()
        POLogger.info(
            message = "Card update is started: waiting for user input.",
            attributes = logAttributes
        )
        dispatch(DidStart)
        resolveScheme()
    }

    private fun initState() = with(configuration) {
        CardUpdateState(
            title = title ?: app.getString(R.string.po_card_update_title),
            fields = POImmutableList(initFields()),
            focusedFieldId = CardFieldId.CVC,
            primaryAction = POActionState(
                id = ActionId.SUBMIT,
                text = submitButton.text ?: app.getString(R.string.po_card_update_button_submit),
                primary = true,
                icon = submitButton.icon
            ),
            secondaryAction = cancelButton?.let {
                POActionState(
                    id = ActionId.CANCEL,
                    text = it.text ?: app.getString(R.string.po_card_update_button_cancel),
                    primary = false,
                    icon = it.icon,
                    confirmation = it.confirmation?.run {
                        Confirmation(
                            title = title ?: app.getString(R.string.po_cancel_confirmation_title),
                            message = message,
                            confirmActionText = confirmActionText
                                ?: app.getString(R.string.po_cancel_confirmation_confirm),
                            dismissActionText = dismissActionText
                                ?: app.getString(R.string.po_cancel_confirmation_dismiss)
                        )
                    }
                )
            },
            draggable = bottomSheet.cancellation.dragDown || bottomSheet.expandable
        )
    }

    private fun initFields(): List<FieldState> {
        val fields = mutableListOf<FieldState>()
        cardNumberField()?.let { fields.add(it) }
        fields.add(cvcField())
        return fields
    }

    private fun cardNumberField(): FieldState? =
        with(configuration.cardInformation) {
            this?.maskedNumber?.let { maskedNumber ->
                if (maskedNumber.isBlank()) return null
                FieldState(
                    id = CardFieldId.NUMBER,
                    forceTextDirectionLtr = true,
                    value = TextFieldValue(text = maskedNumber),
                    enabled = false,
                    iconResId = cardSchemeDrawableResId(
                        scheme = preferredScheme ?: scheme ?: String()
                    )
                )
            }
        }

    private fun cvcField() = FieldState(
        id = CardFieldId.CVC,
        label = app.getString(R.string.po_card_update_cvc),
        forceTextDirectionLtr = true,
        iconResId = com.processout.sdk.ui.R.drawable.po_card_back,
        inputFilter = CardSecurityCodeInputFilter(scheme = configuration.cardInformation?.scheme),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        keyboardActionId = ActionId.SUBMIT
    )

    private fun resolveScheme() {
        with(configuration.cardInformation) {
            if (this?.scheme == null) {
                POLogger.info(
                    message = "Attempt to resolve card scheme.",
                    attributes = logAttributes
                )
                val iin = this?.iin ?: this?.maskedNumber?.let { iin(maskedNumber = it) }
                iin?.let { iin ->
                    viewModelScope.launch {
                        cardsRepository.fetchIssuerInformation(iin)
                            .onSuccess { updateScheme(it.scheme) }
                            .onFailure { failure ->
                                POLogger.info(
                                    message = "Failed to resolve card scheme: %s", failure,
                                    attributes = logAttributes
                                )
                            }
                    }
                }.orElse {
                    POLogger.info(
                        message = "Failed to resolve card scheme: IIN is not available.",
                        attributes = logAttributes
                    )
                }
            }
        }
    }

    private fun iin(maskedNumber: String): String? =
        "^([0-9]{8})".toRegex().find(maskedNumber)?.value
            ?: "^([0-9]{6})".toRegex().find(maskedNumber)?.value

    private fun updateScheme(scheme: String) {
        _state.update {
            it.copy(
                fields = POImmutableList(
                    it.fields.elements.map { field ->
                        when (field.id) {
                            CardFieldId.NUMBER -> field.copy(
                                iconResId = cardSchemeDrawableResId(scheme)
                            )
                            CardFieldId.CVC -> {
                                val inputFilter = CardSecurityCodeInputFilter(scheme = scheme)
                                field.copy(
                                    value = inputFilter.filter(field.value),
                                    inputFilter = inputFilter
                                )
                            }
                            else -> field
                        }
                    }
                )
            )
        }
        POLogger.info(
            message = "Card scheme is resolved: %s", scheme,
            attributes = logAttributes
        )
    }

    fun onEvent(event: CardUpdateEvent) {
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
        val previousValue = field(id)?.value ?: TextFieldValue()
        val isTextChanged = value.text != previousValue.text
        _state.update {
            it.copy(
                fields = POImmutableList(
                    it.fields.elements.map { field ->
                        updatedField(id, value, field, isTextChanged)
                    }
                )
            )
        }
        if (isTextChanged) {
            POLogger.debug(
                message = "Field is edited by the user: %s", id,
                attributes = logAttributes
            )
            dispatch(ParametersChanged)
            if (isCvcValid()) {
                updateState(
                    submitAllowed = true,
                    submitting = _state.value.submitting,
                    errorMessage = null
                )
            }
        }
    }

    private fun updatedField(
        id: String,
        value: TextFieldValue,
        field: FieldState,
        isTextChanged: Boolean
    ): FieldState =
        if (field.id == id) {
            if (isTextChanged) {
                field.copy(value = value, isError = false)
            } else {
                field.copy(value = value)
            }
        } else field

    private fun updateFieldFocus(id: String, isFocused: Boolean) {
        if (isFocused) {
            _state.update { it.copy(focusedFieldId = id) }
        }
    }

    private fun submit() {
        if (!isCvcValid()) {
            POLogger.debug("Ignored attempt to update the card with invalid CVC.")
            return
        }
        updateState(
            submitAllowed = true,
            submitting = true,
            errorMessage = null
        )
        field(CardFieldId.CVC)?.let {
            updateCard(cvc = it.value.text)
        }
    }

    private fun fields(): List<FieldState> = _state.value.fields.elements

    private fun field(id: String): FieldState? = fields().find { it.id == id }

    private fun isCvcValid(): Boolean = field(CardFieldId.CVC)?.let { !it.isError } ?: false

    private fun updateState(
        submitAllowed: Boolean,
        submitting: Boolean,
        errorMessage: String?,
        fields: List<FieldState>? = null
    ) {
        _state.update { state ->
            with(state) {
                copy(
                    fields = POImmutableList(fields ?: state.fields.elements),
                    primaryAction = primaryAction.copy(
                        enabled = submitAllowed,
                        loading = submitting
                    ),
                    secondaryAction = secondaryAction?.copy(
                        enabled = !submitting
                    ),
                    submitting = submitting,
                    errorMessage = errorMessage
                )
            }
        }
    }

    private fun updateCard(cvc: String) {
        POLogger.info(
            message = "Submitting card information.",
            attributes = logAttributes
        )
        dispatch(WillUpdateCard)
        viewModelScope.launch {
            cardsRepository.updateCard(
                request = POCardUpdateRequest(
                    cardId = configuration.cardId,
                    cvc = cvc
                )
            ).onSuccess { card ->
                POLogger.info(
                    message = "Card updated successfully.",
                    attributes = logAttributes
                )
                dispatch(DidComplete)
                _completion.update { Success(card) }
            }.onFailure { failure ->
                requestIfShouldContinue(failure)
            }
        }
    }

    private fun requestIfShouldContinue(failure: ProcessOutResult.Failure) {
        viewModelScope.launch {
            val request = POCardUpdateShouldContinueRequest(
                cardId = configuration.cardId,
                failure = failure
            )
            latestShouldContinueRequest = request
            eventDispatcher.send(request)
            POLogger.info(
                message = "Requested to decide whether the flow should continue or complete after the failure: %s", failure,
                attributes = logAttributes
            )
        }
    }

    private fun shouldContinueOnFailure() {
        eventDispatcher.subscribeForResponse<POCardUpdateShouldContinueResponse>(
            coroutineScope = viewModelScope
        ) { response ->
            if (response.uuid == latestShouldContinueRequest?.uuid) {
                latestShouldContinueRequest = null
                if (response.shouldContinue) {
                    handle(response.failure)
                } else {
                    POLogger.info(
                        message = "Completed after the failure: %s", response.failure,
                        attributes = logAttributes
                    )
                    _completion.update { Failure(response.failure) }
                }
            }
        }
    }

    private fun handle(failure: ProcessOutResult.Failure) {
        val invalidFieldIds = mutableSetOf<String>()
        val errorMessage = when (val code = failure.code) {
            is Generic -> when (code.genericCode) {
                requestInvalidCard,
                cardInvalid,
                cardBadTrackData,
                cardMissingCvc,
                cardInvalidCvc,
                cardFailedCvc,
                cardFailedCvcAndAvs -> {
                    invalidFieldIds.add(CardFieldId.CVC)
                    app.getString(R.string.po_card_update_error_cvc)
                }
                else -> app.getString(R.string.po_card_update_error_generic)
            }
            else -> app.getString(R.string.po_card_update_error_generic)
        }
        val fields = _state.value.fields.elements.map { field ->
            validatedField(invalidFieldIds, field)
        }
        val isCvcValid = fields.find { it.id == CardFieldId.CVC }?.let { !it.isError } ?: false
        updateState(
            submitAllowed = isCvcValid,
            submitting = false,
            errorMessage = errorMessage,
            fields = fields
        )
        POLogger.info(
            message = "Recovered after the failure: %s", failure,
            attributes = logAttributes
        )
    }

    private fun validatedField(invalidFieldIds: Set<String>, field: FieldState): FieldState =
        if (invalidFieldIds.contains(field.id)) {
            field.copy(
                isError = true,
                value = field.value.copy(selection = TextRange(field.value.text.length))
            )
        } else field

    private fun cancel() {
        _completion.update {
            Failure(
                ProcessOutResult.Failure(
                    code = Cancelled,
                    message = "Cancelled by the user with secondary cancel action."
                ).also {
                    POLogger.info(
                        message = "Cancelled: %s", it,
                        attributes = logAttributes
                    )
                }
            )
        }
    }

    private fun dispatch(event: POCardUpdateEvent) {
        viewModelScope.launch {
            eventDispatcher.send(event)
        }
    }

    private fun collectFailure() {
        viewModelScope.launch {
            _completion.collect {
                if (it is Failure) {
                    POLogger.warn("%s", it.failure, attributes = logAttributes)
                }
            }
        }
    }
}
