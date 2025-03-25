package com.processout.sdk.ui.card.tokenization

import androidx.compose.runtime.Immutable
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.shared.state.FieldState

@Immutable
internal data class CardTokenizationViewModelState(
    val title: String,
    val sections: POImmutableList<Section>,
    val focusedFieldId: String?,
    val primaryAction: POActionState,
    val secondaryAction: POActionState?,
    val scanAction: POActionState?,
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
        data class TextField(val state: FieldState) : Item
        data class DropdownField(val state: FieldState) : Item
        data class CheckboxField(val state: FieldState) : Item
        data class Group(val items: POImmutableList<Item>) : Item
    }

    object SectionId {
        const val CARD_INFORMATION = "card-information"
        const val BILLING_ADDRESS = "billing-address"
        const val FUTURE_PAYMENTS = "future-payments"
    }
}
