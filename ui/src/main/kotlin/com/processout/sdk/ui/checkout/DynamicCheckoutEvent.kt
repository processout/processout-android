package com.processout.sdk.ui.checkout

import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.core.ProcessOutResult
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

    data class PermissionRequestResult(
        val paymentMethodId: String,
        val permission: String,
        val isGranted: Boolean
    ) : DynamicCheckoutEvent

    data class Dismiss(
        val failure: ProcessOutResult.Failure
    ) : DynamicCheckoutEvent
}

internal sealed interface DynamicCheckoutSideEffect {
    data class GooglePay(
        val paymentDataRequest: JSONObject
    ) : DynamicCheckoutSideEffect

    data class AlternativePayment(
        val redirectUrl: String,
        val returnUrl: String
    ) : DynamicCheckoutSideEffect

    data class PermissionRequest(
        val paymentMethodId: String,
        val permission: String
    ) : DynamicCheckoutSideEffect
}

internal sealed interface DynamicCheckoutCompletion {
    data object Awaiting : DynamicCheckoutCompletion
    data object Success : DynamicCheckoutCompletion
    data class Failure(val failure: ProcessOutResult.Failure) : DynamicCheckoutCompletion
}
