package com.processout.sdk.ui.checkout

import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.*
import com.processout.sdk.api.model.response.POInvoice

internal data class DynamicCheckoutInteractorState(
    val loading: Boolean,
    val invoice: POInvoice,
    val isInvoiceValid: Boolean,
    val paymentMethods: List<PaymentMethod>,
    val submitActionId: String,
    val cancelActionId: String,
    val selectedPaymentMethodId: String? = null,
    val processingPaymentMethodId: String? = null,
    val pendingSubmitPaymentMethodId: String? = null,
    val errorMessage: String? = null
) {

    sealed interface PaymentMethod {

        val id: String

        data class Card(
            override val id: String,
            val configuration: CardConfiguration,
            val display: Display
        ) : PaymentMethod

        data class GooglePay(
            override val id: String,
            val configuration: GooglePayConfiguration
        ) : PaymentMethod

        data class AlternativePayment(
            override val id: String,
            val redirectUrl: String,
            val display: Display,
            val isExpress: Boolean
        ) : PaymentMethod

        data class NativeAlternativePayment(
            override val id: String,
            val gatewayConfigurationId: String,
            val display: Display
        ) : PaymentMethod

        data class CustomerToken(
            override val id: String,
            val configuration: CustomerTokenConfiguration,
            val display: Display,
            val isExpress: Boolean
        ) : PaymentMethod
    }

    object ActionId {
        const val SUBMIT = "dc-submit"
        const val CANCEL = "dc-cancel"
    }
}
