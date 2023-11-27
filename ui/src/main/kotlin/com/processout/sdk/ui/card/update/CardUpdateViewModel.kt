package com.processout.sdk.ui.card.update

import android.app.Application
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.core.POFailure.Code.*
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
            this?.maskedNumber?.let {
                if (it.isBlank()) return@with null
                POFieldState(
                    key = Field.Number.key,
                    value = format(cardNumber = it),
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

    private fun format(cardNumber: String) =
        cardNumber.replace(".{4}(?!$)".toRegex(), "$0 ")

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
                        if (it.key == key) it.copy(value = value) else it.copy()
                    }
                )
            )
        }
    }

    // TODO
    private fun submit() {
        _state.update {
            it.copy(
                primaryAction = it.primaryAction.copy(
                    loading = true
                )
            )
        }
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
