package com.processout.sdk.ui.card.tokenization

import androidx.compose.runtime.Immutable
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POFieldState
import com.processout.sdk.ui.core.state.POImmutableList

@Immutable
internal data class CardTokenizationViewModelState(
    val title: String,
    val sections: POImmutableList<Section>,
    val focusedFieldId: String?,
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

    object SectionId {
        const val CARD_INFORMATION = "card-information"
        const val BILLING_ADDRESS = "billing-address"
    }
}
