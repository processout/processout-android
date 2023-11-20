package com.processout.sdk.ui.card.update

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.core.POFailure.Code.*
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.R
import com.processout.sdk.ui.card.update.CardUpdateCompletionState.*
import com.processout.sdk.ui.card.update.CardUpdateEvent.*
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POFieldState
import kotlinx.coroutines.flow.*

internal class CardUpdateViewModel(
    private val app: Application,
    private val cardId: String,
    private val cardsRepository: POCardsRepository
) : ViewModel() {

    class Factory(
        private val app: Application,
        private val cardId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CardUpdateViewModel(
                app = app,
                cardId = cardId,
                cardsRepository = ProcessOut.instance.cards
            ) as T
    }

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    private val _completionState = MutableStateFlow<CardUpdateCompletionState>(Awaiting)
    val completionState = _completionState.asStateFlow()

    // TODO: init proper defaults
    private fun initState() = CardUpdateState(
        title = "Title",
        cardField = POFieldState(
            title = "Field 1"
        ),
        cvcField = POFieldState(
            title = "Field 2"
        ),
        primaryAction = POActionState(
            text = app.getString(R.string.po_card_update_button_submit),
            primary = true
        ),
        secondaryAction = POActionState(
            text = app.getString(R.string.po_card_update_button_cancel),
            primary = false
        )
    )

    fun onEvent(event: CardUpdateEvent) = when (event) {
        Submit -> submit()
        Cancel -> POLogger.info("Cancel")
    }

    private fun submit() {
        POLogger.info("Submit")
        _state.update {
            it.copy(
                primaryAction = it.primaryAction.copy(
                    loading = true
                )
            )
        }
    }
}
