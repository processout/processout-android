package com.processout.sdk.ui.card.tokenization

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POFieldState
import com.processout.sdk.ui.core.state.POImmutableList

@Immutable
internal data class CardTokenizationState(
    val title: String,
    val sections: POImmutableList<Section>,
    val primaryAction: POActionState,
    val secondaryAction: POActionState?,
    val draggable: Boolean
) {

    @Immutable
    data class Section(
        val items: POImmutableList<Item>
    )

    @Immutable
    sealed interface Item {
        data class TextField(val state: POFieldState) : Item
        data class Group(val items: POImmutableList<Item>) : Item
    }
}

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
