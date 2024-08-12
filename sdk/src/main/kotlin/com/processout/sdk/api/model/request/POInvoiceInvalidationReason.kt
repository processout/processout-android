package com.processout.sdk.api.model.request

import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
sealed class POInvoiceInvalidationReason {
    data object PaymentMethodChanged : POInvoiceInvalidationReason()
    data class Error(val failure: ProcessOutResult.Failure) : POInvoiceInvalidationReason()
    data object Other : POInvoiceInvalidationReason()
}
