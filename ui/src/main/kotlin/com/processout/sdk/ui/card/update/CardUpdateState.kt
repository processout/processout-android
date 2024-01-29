package com.processout.sdk.ui.card.update

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.core.state.POActionState

@Immutable
internal data class CardUpdateState(
    val title: String,
    val primaryAction: POActionState,
    val secondaryAction: POActionState?,
    val focusedFieldKey: String? = null,
    val errorMessage: String? = null,
    val draggable: Boolean
)

internal sealed interface CardUpdateEvent {
    data class FieldValueChanged(val key: String, val value: TextFieldValue) : CardUpdateEvent
    data class FieldFocusChanged(val key: String, val isFocused: Boolean) : CardUpdateEvent
    data class Action(val key: String) : CardUpdateEvent
    data class Dismiss(val failure: ProcessOutResult.Failure) : CardUpdateEvent
}

internal sealed interface CardUpdateCompletion {
    data object Awaiting : CardUpdateCompletion
    data class Success(val card: POCard) : CardUpdateCompletion
    data class Failure(val failure: ProcessOutResult.Failure) : CardUpdateCompletion
}
