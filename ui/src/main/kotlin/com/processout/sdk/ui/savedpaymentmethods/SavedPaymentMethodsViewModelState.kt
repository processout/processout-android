package com.processout.sdk.ui.savedpaymentmethods

import androidx.compose.runtime.Immutable
import com.processout.sdk.api.model.response.POImageResource
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
            val paymentMethods: POImmutableList<PaymentMethod>,
            val errorMessage: String?
        ) : Content

        data class Empty(
            val message: String,
            val description: String
        ) : Content
    }

    @Immutable
    data class PaymentMethod(
        val id: String,
        val logo: POImageResource,
        val description: String,
        val deleteAction: POActionState?
    )
}
