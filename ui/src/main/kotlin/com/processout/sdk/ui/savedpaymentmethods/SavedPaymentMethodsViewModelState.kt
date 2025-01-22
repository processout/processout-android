package com.processout.sdk.ui.savedpaymentmethods

import androidx.compose.runtime.Immutable
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList

@Immutable
internal data class SavedPaymentMethodsViewModelState(
    val title: String,
    val cancelAction: POActionState?,
    val content: Content,
    val draggable: Boolean
) {

    @Immutable
    sealed interface Content {
        data object Starting : Content

        data class Started(
            val paymentMethods: POImmutableList<String>
        ) : Content

        data class Empty(
            val message: String,
            val description: String
        ) : Content
    }
}
