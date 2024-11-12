package com.processout.sdk.ui.checkout

import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.*
import com.processout.sdk.api.model.response.POInvoice
import org.json.JSONObject

internal data class DynamicCheckoutInteractorState(
    val loading: Boolean,
    val invoice: POInvoice,
    val isInvoiceValid: Boolean,
    val paymentMethods: List<PaymentMethod>,
    val submitActionId: String,
    val cancelActionId: String,
    val selectedPaymentMethod: PaymentMethod? = null,
    val processingPaymentMethod: PaymentMethod? = null,
    val pendingSubmitPaymentMethod: PaymentMethod? = null,
    val errorMessage: String? = null,
    val delayedSuccess: Boolean = false
) {

    sealed interface PaymentMethod {

        val id: String
        val original: PODynamicCheckoutPaymentMethod

        data class Card(
            override val id: String,
            override val original: PODynamicCheckoutPaymentMethod,
            val configuration: CardConfiguration,
            val display: Display
        ) : PaymentMethod

        data class GooglePay(
            override val id: String,
            override val original: PODynamicCheckoutPaymentMethod,
            val allowedPaymentMethods: String,
            val paymentDataRequest: JSONObject
        ) : PaymentMethod

        data class AlternativePayment(
            override val id: String,
            override val original: PODynamicCheckoutPaymentMethod,
            val redirectUrl: String,
            val savingAllowed: Boolean,
            val display: Display,
            val isExpress: Boolean
        ) : PaymentMethod

        data class NativeAlternativePayment(
            override val id: String,
            override val original: PODynamicCheckoutPaymentMethod,
            val gatewayConfigurationId: String,
            val display: Display
        ) : PaymentMethod

        data class CustomerToken(
            override val id: String,
            override val original: PODynamicCheckoutPaymentMethod,
            val configuration: CustomerTokenConfiguration,
            val display: Display,
            val isExpress: Boolean
        ) : PaymentMethod
    }

    object PaymentMethodId {
        const val CARD = "card"
    }

    object ActionId {
        const val SUBMIT = "dc-submit"
        const val CANCEL = "dc-cancel"
    }
}
