package com.processout.sdk.ui.checkout

import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.api.model.response.POGooglePayCardTokenizationData
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.savedpaymentmethods.POSavedPaymentMethodsConfiguration
import org.json.JSONObject

internal sealed interface DynamicCheckoutEvent {
    data class PaymentMethodSelected(
        val id: String
    ) : DynamicCheckoutEvent

    data class FieldValueChanged(
        val paymentMethodId: String,
        val fieldId: String,
        val value: TextFieldValue
    ) : DynamicCheckoutEvent

    data class FieldFocusChanged(
        val paymentMethodId: String,
        val fieldId: String,
        val isFocused: Boolean
    ) : DynamicCheckoutEvent

    data class Action(
        val actionId: String,
        val paymentMethodId: String?
    ) : DynamicCheckoutEvent

    data class ActionConfirmationRequested(
        val id: String
    ) : DynamicCheckoutEvent

    data class DialogAction(
        val actionId: String,
        val paymentMethodId: String?,
        val isConfirmed: Boolean
    ) : DynamicCheckoutEvent

    data class GooglePayResult(
        val paymentMethodId: String,
        val result: ProcessOutResult<POGooglePayCardTokenizationData>
    ) : DynamicCheckoutEvent

    data class AlternativePaymentResult(
        val paymentMethodId: String,
        val result: ProcessOutResult<POAlternativePaymentMethodResponse>
    ) : DynamicCheckoutEvent

    data class PermissionRequestResult(
        val paymentMethodId: String,
        val permission: String,
        val isGranted: Boolean
    ) : DynamicCheckoutEvent

    data class CustomerTokenDeleted(
        val tokenId: String
    ) : DynamicCheckoutEvent

    data class Dismiss(
        val failure: ProcessOutResult.Failure
    ) : DynamicCheckoutEvent
}

internal sealed interface DynamicCheckoutSideEffect {
    data class GooglePay(
        val paymentMethodId: String,
        val paymentDataRequest: JSONObject
    ) : DynamicCheckoutSideEffect

    data class AlternativePayment(
        val paymentMethodId: String,
        val redirectUrl: String,
        val returnUrl: String
    ) : DynamicCheckoutSideEffect

    data class SavedPaymentMethods(
        val configuration: POSavedPaymentMethodsConfiguration
    ) : DynamicCheckoutSideEffect

    data class PermissionRequest(
        val paymentMethodId: String,
        val permission: String
    ) : DynamicCheckoutSideEffect

    data object CancelWebAuthorization : DynamicCheckoutSideEffect

    data object BeforeSuccess : DynamicCheckoutSideEffect
}

internal sealed interface DynamicCheckoutCompletion {
    data object Awaiting : DynamicCheckoutCompletion
    data object Success : DynamicCheckoutCompletion
    data class Failure(val failure: ProcessOutResult.Failure) : DynamicCheckoutCompletion
}
