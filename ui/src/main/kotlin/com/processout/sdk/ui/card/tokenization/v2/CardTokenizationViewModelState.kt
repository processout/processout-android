package com.processout.sdk.ui.card.tokenization.v2

import androidx.compose.runtime.Immutable
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POFieldState
import com.processout.sdk.ui.core.state.POImmutableList

@Immutable
internal data class CardTokenizationViewModelState(
    val title: String,
    val sections: POImmutableList<Section>,
    val primaryAction: POActionState,
    val secondaryAction: POActionState?,
    val focusedFieldId: String? = null,
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
