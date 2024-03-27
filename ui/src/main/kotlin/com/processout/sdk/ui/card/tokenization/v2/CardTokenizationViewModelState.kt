package com.processout.sdk.ui.card.tokenization.v2

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POFieldState
import com.processout.sdk.ui.core.state.POImmutableList

@Immutable
internal data class CardTokenizationViewModelState(
    val title: String,
    val sections: POImmutableList<Section>,
    val focusedFieldId: String? = null,
    val primaryAction: POActionState,
    val secondaryAction: POActionState?,
    val draggable: Boolean
) {

    @Immutable
    data class Section(
        val id: String,
        val title: String? = null,
        val items: POImmutableList<Item>,
        val errorMessage: String? = null
    )

    @Immutable
    sealed interface Item {
        data class TextField(val state: POFieldState) : Item
        data class DropdownField(val state: POFieldState) : Item
        data class Group(val items: POImmutableList<Item>) : Item
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
    data class Success(val card: POCard) : CardTokenizationCompletion
    data class Failure(val failure: ProcessOutResult.Failure) : CardTokenizationCompletion
}
