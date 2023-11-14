package com.processout.sdk.ui.card.update

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.core.POFailure.Code.*
import com.processout.sdk.ui.card.update.CardUpdateCompletionState.*
import com.processout.sdk.ui.card.update.CardUpdateEvent.*
import com.processout.sdk.ui.shared.state.FieldState
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
        cardField = FieldState(
            title = "Field 1"
        ),
        cvcField = FieldState(
            title = "Field 2"
        ),
        primaryActionText = "Submit",
        secondaryActionText = "Cancel"
    )

    fun onEvent(event: CardUpdateEvent) = when (event) {
        Submit -> {}
    }
}
