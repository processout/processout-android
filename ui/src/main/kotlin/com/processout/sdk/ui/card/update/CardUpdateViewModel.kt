package com.processout.sdk.ui.card.update

import android.app.Application
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.core.POFailure.Code.*
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.R
import com.processout.sdk.ui.card.update.CardUpdateCompletionState.*
import com.processout.sdk.ui.card.update.CardUpdateEvent.*
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POFieldState
import com.processout.sdk.ui.core.state.POImmutableCollection
import kotlinx.coroutines.flow.*

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

    private fun initFields(): POImmutableCollection<POFieldState> =
        with(options.cardInformation) {
            val fields = mutableListOf<POFieldState>()
            this?.maskedNumber?.let {
                if (it.isNotBlank()) {
                    fields.add(
                        POFieldState(
                            key = Field.Number.key,
                            value = format(cardNumber = it),
                            enabled = false,
                            iconResId = R.drawable.po_scheme_mastercard
                        )
                    )
                }
            }
            fields.add(
                POFieldState(
                    key = Field.CVC.key,
                    placeholder = app.getString(R.string.po_card_update_cvc),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    iconResId = R.drawable.po_card_back
                )
            )
            POImmutableCollection(fields)
        }

    private fun format(cardNumber: String) =
        cardNumber.replace(".{4}(?!$)".toRegex(), "$0 ")

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
