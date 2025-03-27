package com.processout.sdk.ui.checkout

import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.*
import com.processout.sdk.api.model.response.POInvoice
import org.json.JSONObject

internal data class DynamicCheckoutInteractorState(
    val loading: Boolean,
    val invoice: POInvoice?,
    val paymentMethods: List<PaymentMethod>,
    val actions: Actions = Actions(),
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
            val gatewayConfigurationId: String,
            val redirectUrl: String,
            val savePaymentMethodField: Field?,
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

    data class Field(
        val id: String,
        val value: TextFieldValue = TextFieldValue()
    )

    data class Actions(
        val submitId: String = ActionId.SUBMIT,
        val cancelId: String = ActionId.CANCEL,
        val cardScannerId: String = ActionId.CARD_SCANNER,
        val savedPaymentMethodsId: String = ActionId.SAVED_PAYMENT_METHODS
    )

    object PaymentMethodId {
        const val CARD = "card"
    }

    object FieldId {
        const val SAVE_PAYMENT_METHOD = "save-payment-method"
    }

    object ActionId {
        const val SUBMIT = "dc-submit"
        const val CANCEL = "dc-cancel"
        const val CARD_SCANNER = "dc-card-scanner"
        const val SAVED_PAYMENT_METHODS = "dc-saved-payment-methods"
    }
}
