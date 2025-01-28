package com.processout.sdk.ui.savedpaymentmethods

import com.processout.sdk.api.model.response.POImageResource

internal data class SavedPaymentMethodsInteractorState(
    val loading: Boolean,
    val customerId: String?,
    val paymentMethods: List<PaymentMethod>,
    val cancelActionId: String,
    val errorMessage: String? = null
) {

    data class PaymentMethod(
        val customerTokenId: String,
        val logo: POImageResource,
        val name: String,
        val description: String?,
        val deleteAction: Action?
    )

    data class Action(
        val id: String,
        val processing: Boolean
    )

    object ActionId {
        const val DELETE = "delete"
        const val CANCEL = "cancel"
    }
}
