package com.processout.sdk.ui.checkout

import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.core.ProcessOutResult

internal sealed interface DynamicCheckoutExtendedEvent : DynamicCheckoutEvent {
    data class PaymentMethodSelected(val id: String) : DynamicCheckoutExtendedEvent
    data class Dismiss(val failure: ProcessOutResult.Failure) : DynamicCheckoutExtendedEvent
}

internal sealed interface DynamicCheckoutEvent {
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
}

internal sealed interface DynamicCheckoutCompletion {
    data object Awaiting : DynamicCheckoutCompletion
    data object Success : DynamicCheckoutCompletion
    data class Failure(val failure: ProcessOutResult.Failure) : DynamicCheckoutCompletion
}
