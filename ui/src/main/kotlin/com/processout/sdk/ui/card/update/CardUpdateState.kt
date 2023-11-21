package com.processout.sdk.ui.card.update

import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POFieldState

internal data class CardUpdateState(
    val title: String,
    val cardField: POFieldState,
    val cvcField: POFieldState,
    val primaryAction: POActionState,
    val secondaryAction: POActionState?,
    val draggable: Boolean
)

internal sealed interface CardUpdateCompletionState {
    data object Awaiting : CardUpdateCompletionState
    data object Success : CardUpdateCompletionState
    data class Failure(val failure: ProcessOutResult.Failure) : CardUpdateCompletionState
}

internal sealed interface CardUpdateEvent {
    data object Submit : CardUpdateEvent
    data object Cancel : CardUpdateEvent
}
