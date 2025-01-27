package com.processout.sdk.ui.savedpaymentmethods

import com.processout.sdk.api.model.response.POImageResource

internal data class SavedPaymentMethodsInteractorState(
    val loading: Boolean,
    val paymentMethods: List<PaymentMethod>,
    val deleteActionId: String,
    val cancelActionId: String,
    val errorMessage: String? = null
) {

    data class PaymentMethod(
        val id: String,
        val logo: POImageResource,
        val description: String,
        val deletingAllowed: Boolean
    )

    object ActionId {
        const val DELETE = "delete"
        const val CANCEL = "cancel"
    }
}
