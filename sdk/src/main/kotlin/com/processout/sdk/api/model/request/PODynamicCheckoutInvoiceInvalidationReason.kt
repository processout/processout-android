package com.processout.sdk.api.model.request

import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
sealed class PODynamicCheckoutInvoiceInvalidationReason {
    data object PaymentMethodChanged : PODynamicCheckoutInvoiceInvalidationReason()
    data class Failure(
        val failure: ProcessOutResult.Failure
    ) : PODynamicCheckoutInvoiceInvalidationReason()
}
