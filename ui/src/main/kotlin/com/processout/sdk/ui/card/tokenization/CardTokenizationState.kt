package com.processout.sdk.ui.card.tokenization

import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.api.model.response.POCardIssuerInformation
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POMutableFieldState
import com.processout.sdk.ui.core.state.POStableList

@Immutable
internal data class CardTokenizationState(
    val title: String,
    val primaryAction: POActionState,
    val secondaryAction: POActionState?,
    val issuerInformation: POCardIssuerInformation? = null,
    val focusedFieldId: String? = null,
    val draggable: Boolean
)

@Stable
internal data class CardTokenizationSection(
    val id: String,
    val title: String? = null,
    val items: POStableList<Item>
) {
    var errorMessage: String? by mutableStateOf(null)

    @Stable
    sealed interface Item {
        data class TextField(val state: POMutableFieldState) : Item
        data class Group(val items: POStableList<Item>) : Item
    }
}

internal sealed interface CardTokenizationEvent {
    data class FieldValueChanged(val id: String, val value: TextFieldValue) : CardTokenizationEvent
    data class FieldFocusChanged(val id: String, val isFocused: Boolean) : CardTokenizationEvent
    data class Action(val id: String) : CardTokenizationEvent
    data class Dismiss(val failure: ProcessOutResult.Failure) : CardTokenizationEvent
}

internal sealed interface CardTokenizationCompletion {
    data object Awaiting : CardTokenizationCompletion
    data class Success(val data: POCardTokenizationData) : CardTokenizationCompletion
    data class Failure(val failure: ProcessOutResult.Failure) : CardTokenizationCompletion
}
