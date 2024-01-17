package com.processout.sdk.ui.card.tokenization

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.core.state.POActionState

@Immutable
internal data class CardTokenizationState(
    val title: String,
    val primaryAction: POActionState,
    val secondaryAction: POActionState?,
    val draggable: Boolean
)

internal sealed interface CardTokenizationEvent {
    data class FieldValueChanged(val key: String, val value: TextFieldValue) : CardTokenizationEvent
    data object Submit : CardTokenizationEvent
    data object Cancel : CardTokenizationEvent
    data class Dismiss(val failure: ProcessOutResult.Failure) : CardTokenizationEvent
}

internal sealed interface CardTokenizationCompletion {
    data object Awaiting : CardTokenizationCompletion
    data class Success(val response: POCardTokenizationResponse) : CardTokenizationCompletion
    data class Failure(val failure: ProcessOutResult.Failure) : CardTokenizationCompletion
}
