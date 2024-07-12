package com.processout.sdk.ui.checkout

import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.*
import java.util.UUID

internal data class DynamicCheckoutInteractorState(
    val loading: Boolean,
    val paymentMethods: List<PaymentMethod>,
    val selectedPaymentMethodId: String?
) {

    sealed interface PaymentMethod {

        val id: String

        data class Card(
            override val id: String = UUID.randomUUID().toString(),
            val configuration: CardConfiguration,
            val display: Display
        ) : PaymentMethod

        data class GooglePay(
            override val id: String = UUID.randomUUID().toString(),
            val configuration: GooglePayConfiguration
        ) : PaymentMethod

        data class AlternativePayment(
            override val id: String = UUID.randomUUID().toString(),
            val redirectUrl: String,
            val display: Display,
            val isExpress: Boolean
        ) : PaymentMethod

        data class NativeAlternativePayment(
            override val id: String = UUID.randomUUID().toString(),
            val gatewayConfigurationId: String,
            val display: Display
        ) : PaymentMethod
    }
}
