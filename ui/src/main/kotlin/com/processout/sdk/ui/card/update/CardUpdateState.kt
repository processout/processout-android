package com.processout.sdk.ui.card.update

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POFieldState
import com.processout.sdk.ui.core.state.POImmutableList

@Immutable
internal data class CardUpdateState(
    val title: String,
    val fields: POImmutableList<POFieldState>,
    val focusedFieldId: String? = null,
    val primaryAction: POActionState,
    val secondaryAction: POActionState?,
    val submitting: Boolean = false,
    val errorMessage: String? = null,
    val draggable: Boolean
)

internal sealed interface CardUpdateEvent {
    data class FieldValueChanged(val id: String, val value: TextFieldValue) : CardUpdateEvent
    data class FieldFocusChanged(val id: String, val isFocused: Boolean) : CardUpdateEvent
    data class Action(val id: String) : CardUpdateEvent
    data class Dismiss(val failure: ProcessOutResult.Failure) : CardUpdateEvent
}

internal sealed interface CardUpdateCompletion {
    data object Awaiting : CardUpdateCompletion
    data class Success(val card: POCard) : CardUpdateCompletion
    data class Failure(val failure: ProcessOutResult.Failure) : CardUpdateCompletion
}
