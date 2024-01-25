package com.processout.sdk.ui.card.tokenization

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POMutableFieldState
import com.processout.sdk.ui.core.state.POStableList

@Immutable
internal data class CardTokenizationState(
    val title: String,
    val primaryAction: POActionState,
    val secondaryAction: POActionState?,
    val draggable: Boolean
)

@Stable
internal data class CardTokenizationSection(
    val items: POStableList<Item>
) {
    @Stable
    sealed interface Item {
        data class TextField(val state: POMutableFieldState) : Item
        data class Group(val items: POStableList<Item>) : Item
    }
}

internal sealed interface CardTokenizationEvent {
    data class FieldValueChanged(val key: String, val value: TextFieldValue) : CardTokenizationEvent
    data class Action(val key: String) : CardTokenizationEvent
    data class Dismiss(val failure: ProcessOutResult.Failure) : CardTokenizationEvent
}

internal sealed interface CardTokenizationCompletion {
    data object Awaiting : CardTokenizationCompletion
    data class Success(val response: POCardTokenizationResponse) : CardTokenizationCompletion
    data class Failure(val failure: ProcessOutResult.Failure) : CardTokenizationCompletion
}
