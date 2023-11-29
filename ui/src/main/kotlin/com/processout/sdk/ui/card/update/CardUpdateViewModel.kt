package com.processout.sdk.ui.card.update

import android.app.Application
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POCardUpdateRequest
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.core.POFailure.Code.*
import com.processout.sdk.core.POFailure.GenericCode.*
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import com.processout.sdk.ui.R
import com.processout.sdk.ui.card.update.CardUpdateCompletionState.*
import com.processout.sdk.ui.card.update.CardUpdateEvent.*
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POFieldState
import com.processout.sdk.ui.core.state.POImmutableCollection
import com.processout.sdk.ui.shared.formatter.CardSecurityCodeFormatter
import com.processout.sdk.ui.shared.mapper.cardSchemeDrawableResId
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal class CardUpdateViewModel(
    private val app: Application,
    private val cardId: String,
    private val options: POCardUpdateConfiguration.Options,
    private val cardsRepository: POCardsRepository
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
                cardsRepository = ProcessOut.instance.cards
            ) as T
    }

    private enum class Field(val key: String) {
        Number("card-number"),
        CVC("card-cvc")
    }

    private val _completionState = MutableStateFlow<CardUpdateCompletionState>(Awaiting)
    val completionState = _completionState.asStateFlow()

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    init {
        resolveScheme()
    }

    private fun initState() = with(options) {
        CardUpdateState(
            title = title ?: app.getString(R.string.po_card_update_title),
            fields = initFields(),
            primaryAction = POActionState(
                text = primaryActionText ?: app.getString(R.string.po_card_update_button_submit),
                primary = true
            ),
            secondaryAction = if (cancellation.secondaryAction) POActionState(
                text = secondaryActionText ?: app.getString(R.string.po_card_update_button_cancel),
                primary = false
            ) else null,
            draggable = cancellation.dragDown
        )
    }

    private fun initFields(): POImmutableCollection<POFieldState> {
        val fields = mutableListOf<POFieldState>()
        initCardNumberField()?.let { fields.add(it) }
        fields.add(initCvcField())
        return POImmutableCollection(fields)
    }

    private fun initCardNumberField(): POFieldState? =
        with(options.cardInformation) {
            this?.maskedNumber?.let { maskedNumber ->
                if (maskedNumber.isBlank()) return null
                POFieldState(
                    key = Field.Number.key,
                    value = maskedNumber,
                    iconResId = cardSchemeDrawableResId(
                        scheme = preferredScheme ?: scheme ?: String()
                    ),
                    enabled = false
                )
            }
        }

    private fun initCvcField() = POFieldState(
        key = Field.CVC.key,
        placeholder = app.getString(R.string.po_card_update_cvc),
        iconResId = R.drawable.po_card_back,
        formatter = CardSecurityCodeFormatter(scheme = options.cardInformation?.scheme),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        )
    )

    private fun resolveScheme() {
        with(options.cardInformation) {
            if (this?.scheme == null) {
                val iin = this?.iin ?: this?.maskedNumber?.let { iin(it) }
                iin?.let {
                    viewModelScope.launch {
                        cardsRepository.fetchIssuerInformation(it)
                            .onSuccess { updateScheme(it.scheme) }
                            .onFailure {
                                POLogger.info(
                                    "Failed to fetch card issuer information on attempt to resolve the scheme. %s", it
                                )
                            }
                    }
                }
            }
        }
    }

    private fun iin(maskedNumber: String): String? =
        "^([0-9]{8})".toRegex().find(maskedNumber)?.value
            ?: "^([0-9]{6})".toRegex().find(maskedNumber)?.value

    private fun updateScheme(scheme: String) {
        _state.update { state ->
            state.copy(
                fields = POImmutableCollection(
                    state.fields.elements.map {
                        when (it.key) {
                            Field.Number.key -> it.copy(
                                iconResId = cardSchemeDrawableResId(scheme)
                            )
                            Field.CVC.key -> {
                                val formatter = CardSecurityCodeFormatter(scheme = scheme)
                                it.copy(
                                    value = formatter.format(it.value),
                                    formatter = formatter
                                )
                            }
                            else -> it.copy()
                        }
                    }
                )
            )
        }
    }

    fun onEvent(event: CardUpdateEvent) = when (event) {
        is FieldValueChanged -> updateFieldValue(event.key, event.value)
        Submit -> submit()
        Cancel -> cancel()
    }

    private fun updateFieldValue(key: String, value: String) {
        _state.update { state ->
            state.copy(
                fields = POImmutableCollection(
                    state.fields.elements.map {
                        when (it.key) {
                            key -> it.copy(
                                value = value,
                                isError = false
                            )
                            else -> it.copy()
                        }
                    }
                ),
                errorMessage = null
            )
        }
    }

    private fun submit() {
        _state.updateAndGet {
            resolve(state = it, submitting = true)
        }.also { state ->
            state.fields.elements.find { it.key == Field.CVC.key }?.let { cvcField ->
                updateCard(cvcField.value)
            }
        }
    }

    private fun resolve(
        state: CardUpdateState,
        submitting: Boolean,
        errorMessage: String? = null
    ) = state.copy(
        fields = POImmutableCollection(
            state.fields.elements.map {
                when (it.key) {
                    Field.CVC.key -> it.copy(
                        enabled = !submitting,
                        isError = errorMessage != null
                    )
                    else -> it.copy()
                }
            }
        ),
        primaryAction = state.primaryAction.copy(
            loading = submitting
        ),
        secondaryAction = state.secondaryAction?.copy(
            enabled = !submitting
        ),
        errorMessage = errorMessage
    )

    private fun updateCard(cvc: String) {
        viewModelScope.launch {
            cardsRepository.updateCard(
                request = POCardUpdateRequest(cardId = cardId, cvc = cvc)
            ).onSuccess { card ->
                _completionState.update { Success(card) }
            }.onFailure { handle(failure = it) }
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
    }

    private fun recover(errorMessage: String) = _state.update {
        resolve(state = it, submitting = false, errorMessage = errorMessage)
    }

    private fun cancel() {
        _completionState.update {
            Failure(
                ProcessOutResult.Failure(
                    code = Cancelled,
                    message = "Cancelled by user with secondary cancel action."
                )
            )
        }
    }
}
