package com.processout.sdk.ui.savedpaymentmethods

import androidx.compose.runtime.Immutable
import com.processout.sdk.ui.core.state.POImmutableList

@Immutable
internal data class SavedPaymentMethodsViewModelState(
    val title: String,
    val content: Content,
    val draggable: Boolean
) {

    @Immutable
    sealed interface Content {
        data class PaymentMethods(
            val loading: Boolean,
            val paymentMethods: POImmutableList<String>
        ) : Content

        data class Empty(
            val message: String,
            val description: String
        ) : Content
    }
}
