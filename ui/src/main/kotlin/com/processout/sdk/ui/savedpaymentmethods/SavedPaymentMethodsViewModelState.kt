package com.processout.sdk.ui.savedpaymentmethods

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList

@Immutable
internal data class SavedPaymentMethodsViewModelState(
    val title: String,
    val content: Content,
    val cancelAction: POActionState?,
    val draggable: Boolean
) {

    @Immutable
    sealed interface Content {
        data object Loading : Content

        data class Loaded(
            val paymentMethods: POImmutableList<String>
        ) : Content

        data class Empty(
            @DrawableRes
            val imageResId: Int,
            val message: String,
            val description: String
        ) : Content
    }
}
