package com.processout.sdk.ui.card.update

import android.app.Application
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.dispatcher.card.update.PODefaultCardUpdateEventDispatcher
import com.processout.sdk.api.model.event.POCardUpdateEvent
import com.processout.sdk.api.model.event.POCardUpdateEvent.*
import com.processout.sdk.api.model.request.POCardUpdateRequest
import com.processout.sdk.api.model.request.POCardUpdateShouldContinueRequest
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.core.POFailure.Code.Cancelled
import com.processout.sdk.core.POFailure.Code.Generic
import com.processout.sdk.core.POFailure.GenericCode.*
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import com.processout.sdk.ui.card.update.CardUpdateCompletion.*
import com.processout.sdk.ui.card.update.CardUpdateEvent.*
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POMutableFieldState
import com.processout.sdk.ui.core.state.POStableList
import com.processout.sdk.ui.shared.extension.orElse
import com.processout.sdk.ui.shared.filter.CardSecurityCodeInputFilter
import com.processout.sdk.ui.shared.provider.cardSchemeDrawableResId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class CardUpdateViewModel(
    private val app: Application,
    private val cardId: String,
    private val options: POCardUpdateConfiguration.Options,
    private val cardsRepository: POCardsRepository,
    private val eventDispatcher: PODefaultCardUpdateEventDispatcher,
    private val logAttributes: Map<String, String>
) : ViewModel() {

    class Factory(
        private val app: Application,
        private val cardId: String,
        private val options: POCardUpdateConfiguration.Options
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CardUpdateViewModel(
                app = app,
                cardId = cardId,
                options = options,
                cardsRepository = ProcessOut.instance.cards,
                eventDispatcher = PODefaultCardUpdateEventDispatcher,
                logAttributes = mapOf(LOG_ATTRIBUTE_CARD_ID to cardId)
            ) as T
    }

    private companion object {
        const val LOG_ATTRIBUTE_CARD_ID = "CardId"
    }

    private object CardFieldKey {
        const val NUMBER = "card-number"
        const val CVC = "card-cvc"
    }

    private object ActionKey {
        const val SUBMIT = "submit"
        const val CANCEL = "cancel"
    }

    private val _completion = MutableStateFlow<CardUpdateCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    private val _fields = mutableStateListOf<POMutableFieldState>().apply { addAll(initFields()) }
    val fields = POStableList(_fields)

    private val shouldContinueRequests = mutableSetOf<POCardUpdateShouldContinueRequest>()

    init {
        handleIfShouldContinue()
        POLogger.info(
            message = "Card update is started: waiting for user input.",
            attributes = logAttributes
        )
        dispatch(DidStart)
        resolveScheme()
    }

    private fun initState() = with(options) {
        CardUpdateState(
            title = title ?: app.getString(R.string.po_card_update_title),
            primaryAction = POActionState(
                key = ActionKey.SUBMIT,
                text = primaryActionText ?: app.getString(R.string.po_card_update_button_submit),
                primary = true
            ),
            secondaryAction = if (cancellation.secondaryAction) POActionState(
                key = ActionKey.CANCEL,
                text = secondaryActionText ?: app.getString(R.string.po_card_update_button_cancel),
                primary = false
            ) else null,
            focusedFieldKey = CardFieldKey.CVC,
            draggable = cancellation.dragDown
        )
    }

    private fun initFields(): List<POMutableFieldState> {
        val fields = mutableListOf<POMutableFieldState>()
        cardNumberField()?.let { fields.add(it) }
        fields.add(cvcField())
        return fields
    }

    private fun cardNumberField(): POMutableFieldState? =
        with(options.cardInformation) {
            this?.maskedNumber?.let { maskedNumber ->
                if (maskedNumber.isBlank()) return null
                POMutableFieldState(
                    key = CardFieldKey.NUMBER,
                    forceTextDirectionLtr = true,
                    value = TextFieldValue(text = maskedNumber),
                    enabled = false,
                    iconResId = cardSchemeDrawableResId(
                        scheme = preferredScheme ?: scheme ?: String()
                    )
                )
            }
        }

    private fun cvcField() = POMutableFieldState(
        key = CardFieldKey.CVC,
        placeholder = app.getString(R.string.po_card_update_cvc),
        forceTextDirectionLtr = true,
        iconResId = com.processout.sdk.ui.R.drawable.po_card_back,
        inputFilter = CardSecurityCodeInputFilter(scheme = options.cardInformation?.scheme),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        keyboardActionKey = ActionKey.SUBMIT
    )

    private fun field(key: String) = _fields.find { it.key == key }

    private fun resolveScheme() {
        with(options.cardInformation) {
            if (this?.scheme == null) {
                POLogger.info(
                    message = "Attempt to resolve card scheme.",
                    attributes = logAttributes
                )
                val iin = this?.iin ?: this?.maskedNumber?.let { iin(it) }
                iin?.let {
                    viewModelScope.launch {
                        cardsRepository.fetchIssuerInformation(it)
                            .onSuccess { updateScheme(it.scheme) }
                            .onFailure {
                                POLogger.info(
                                    message = "Failed to resolve card scheme: %s", it,
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
        _fields.forEach {
            when (it.key) {
                CardFieldKey.NUMBER -> it.apply {
                    iconResId = cardSchemeDrawableResId(scheme)
                }
                CardFieldKey.CVC -> it.apply {
                    val inputFilter = CardSecurityCodeInputFilter(scheme = scheme)
                    value = inputFilter.filter(value)
                    this.inputFilter = inputFilter
                }
            }
        }
        POLogger.info(
            message = "Card scheme is resolved: %s", scheme,
            attributes = logAttributes
        )
    }

    fun onEvent(event: CardUpdateEvent) {
        when (event) {
            is FieldValueChanged -> updateFieldValue(event.key, event.value)
            is FieldFocusChanged -> updateFieldFocus(event.key, event.isFocused)
            is Action -> when (event.key) {
                ActionKey.SUBMIT -> submit()
                ActionKey.CANCEL -> cancel()
            }
            is Dismiss -> POLogger.info(
                message = "Dismissed: %s", event.failure,
                attributes = logAttributes
            )
        }
    }

    private fun updateFieldValue(key: String, value: TextFieldValue) {
        field(key)?.apply {
            this.value = value
            isError = false
        }
        _state.update { state ->
            state.copy(
                primaryAction = state.primaryAction.copy(
                    enabled = true
                ),
                errorMessage = null
            )
        }
        POLogger.debug(
            message = "Field is edited by the user: %s", key,
            attributes = logAttributes
        )
        dispatch(ParametersChanged)
    }

    private fun updateFieldFocus(key: String, isFocused: Boolean) {
        if (isFocused) {
            _state.update { it.copy(focusedFieldKey = key) }
        }
    }

    private fun submit() {
        _state.update {
            resolve(state = it, submitting = true)
        }
        field(CardFieldKey.CVC)?.let {
            updateCard(cvc = it.value.text)
        }
    }

    private fun resolve(
        state: CardUpdateState,
        submitting: Boolean,
        errorMessage: String? = null
    ): CardUpdateState {
        field(CardFieldKey.CVC)?.apply {
            isError = errorMessage != null
        }
        return state.copy(
            primaryAction = state.primaryAction.copy(
                enabled = errorMessage == null,
                loading = submitting
            ),
            secondaryAction = state.secondaryAction?.copy(
                enabled = !submitting
            ),
            errorMessage = errorMessage
        )
    }

    private fun updateCard(cvc: String) {
        POLogger.info(
            message = "Submitting card information.",
            attributes = logAttributes
        )
        dispatch(WillUpdateCard)
        viewModelScope.launch {
            cardsRepository.updateCard(
                request = POCardUpdateRequest(cardId = cardId, cvc = cvc)
            ).onSuccess { card ->
                POLogger.info(
                    message = "Card updated successfully.",
                    attributes = logAttributes
                )
                dispatch(DidComplete)
                _completion.update { Success(card) }
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
            val request = POCardUpdateShouldContinueRequest(cardId, failure)
            shouldContinueRequests.add(request)
            eventDispatcher.send(request)
            POLogger.info(
                message = "Requested to decide whether the flow should continue or complete after the failure: %s", failure,
                attributes = logAttributes
            )
        }
    }

    private fun handleIfShouldContinue() {
        viewModelScope.launch {
            eventDispatcher.shouldContinueResponse.collect { response ->
                if (shouldContinueRequests.removeAll { it.uuid == response.uuid }) {
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
    }

    private fun handle(failure: ProcessOutResult.Failure) {
        val genericErrorMessage = app.getString(R.string.po_card_update_error_generic)
        when (val code = failure.code) {
            is Generic -> when (code.genericCode) {
                requestInvalidCard,
                cardInvalid,
                cardBadTrackData,
                cardMissingCvc,
                cardInvalidCvc,
                cardFailedCvc,
                cardFailedCvcAndAvs -> recover(app.getString(R.string.po_card_update_error_cvc))
                else -> recover(genericErrorMessage)
            }
            else -> recover(genericErrorMessage)
        }
        POLogger.info(
            message = "Recovered after the failure: %s", failure,
            attributes = logAttributes
        )
    }

    private fun recover(errorMessage: String) {
        _state.update {
            resolve(
                state = it,
                submitting = false,
                errorMessage = errorMessage
            )
        }
    }

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
}
