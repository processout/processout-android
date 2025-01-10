package com.processout.sdk.ui.savedpaymentmethods

import com.processout.sdk.api.model.response.POInvoice

internal data class SavedPaymentMethodsInteractorState(
    val loading: Boolean,
    val invoice: POInvoice?,
    val deleteActionId: String,
    val cancelActionId: String,
    val errorMessage: String? = null
) {

    object ActionId {
        const val DELETE = "delete"
        const val CANCEL = "cancel"
    }
}
