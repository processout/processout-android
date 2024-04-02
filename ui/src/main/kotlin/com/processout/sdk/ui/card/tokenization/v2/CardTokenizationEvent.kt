package com.processout.sdk.ui.card.tokenization.v2

import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.core.ProcessOutResult

internal sealed interface CardTokenizationEvent {
    data class FieldValueChanged(val id: String, val value: TextFieldValue) : CardTokenizationEvent
    data class FieldFocusChanged(val id: String, val isFocused: Boolean) : CardTokenizationEvent
    data class Action(val id: String) : CardTokenizationEvent
    data class Dismiss(val failure: ProcessOutResult.Failure) : CardTokenizationEvent
}

internal sealed interface CardTokenizationCompletion {
    data object Awaiting : CardTokenizationCompletion
    data class Success(val card: POCard) : CardTokenizationCompletion
    data class Failure(val failure: ProcessOutResult.Failure) : CardTokenizationCompletion
}
