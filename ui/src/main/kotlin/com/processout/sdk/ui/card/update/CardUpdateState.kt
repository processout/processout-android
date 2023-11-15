package com.processout.sdk.ui.card.update

import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.shared.state.FieldState

internal data class CardUpdateState(
    val title: String,
    val cardField: FieldState,
    val cvcField: FieldState,
    val primaryActionText: String,
    val secondaryActionText: String,
    val submitting: Boolean = false
)

internal sealed interface CardUpdateCompletionState {
    data object Awaiting : CardUpdateCompletionState
    data object Success : CardUpdateCompletionState
    data class Failure(val failure: ProcessOutResult.Failure) : CardUpdateCompletionState
}

internal sealed interface CardUpdateEvent {
    data object Submit : CardUpdateEvent
}
