package com.processout.sdk.ui.card.tokenization

import android.app.Application
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.card.tokenization.CardTokenizationCompletion.Awaiting
import com.processout.sdk.ui.card.tokenization.CardTokenizationCompletion.Failure
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent.*
import com.processout.sdk.ui.core.state.POActionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class CardTokenizationViewModel(
    private val app: Application,
    private val configuration: POCardTokenizationConfiguration,
    private val cardsRepository: POCardsRepository
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
                cardsRepository = ProcessOut.instance.cards
            ) as T
    }

    private val _completion = MutableStateFlow<CardTokenizationCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    private fun initState() = with(configuration) {
        CardTokenizationState(
            title = title ?: app.getString(R.string.po_card_tokenization_title),
            primaryAction = POActionState(
                text = primaryActionText ?: app.getString(R.string.po_card_tokenization_button_submit),
                primary = true
            ),
            secondaryAction = if (cancellation.secondaryAction) POActionState(
                text = secondaryActionText ?: app.getString(R.string.po_card_tokenization_button_cancel),
                primary = false
            ) else null,
            draggable = cancellation.dragDown
        )
    }

    fun onEvent(event: CardTokenizationEvent) = when (event) {
        is FieldValueChanged -> updateFieldValue(event.key, event.value)
        Submit -> submit()
        Cancel -> cancel()
        is Dismiss -> POLogger.info("Dismissed: %s", event.failure)
    }

    private fun updateFieldValue(key: String, value: TextFieldValue) {
        // TODO
    }

    private fun submit() {
        // TODO
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
}
