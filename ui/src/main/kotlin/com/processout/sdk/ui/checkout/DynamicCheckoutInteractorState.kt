package com.processout.sdk.ui.checkout

import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.*
import com.processout.sdk.api.model.response.POInvoice

internal data class DynamicCheckoutInteractorState(
    val loading: Boolean,
    val invoice: POInvoice,
    val paymentMethods: List<PaymentMethod>,
    val selectedPaymentMethodId: String?,
    val cancelActionId: String,
    val errorMessage: String? = null,
    val isInvoiceValid: Boolean = true
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
    }

    object ActionId {
        const val CANCEL = "cancel"
    }
}
