package com.processout.sdk.ui.savedpaymentmethods

import com.processout.sdk.core.ProcessOutResult

internal sealed interface SavedPaymentMethodsEvent {
    data class Action(
        val actionId: String,
        val paymentMethodId: String?
    ) : SavedPaymentMethodsEvent

    data class Dismiss(
        val failure: ProcessOutResult.Failure
    ) : SavedPaymentMethodsEvent
}

internal sealed interface SavedPaymentMethodsCompletion {
    data object Awaiting : SavedPaymentMethodsCompletion
    data object Success : SavedPaymentMethodsCompletion
    data class Failure(val failure: ProcessOutResult.Failure) : SavedPaymentMethodsCompletion
}
