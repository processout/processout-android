package com.processout.sdk.api.model.request

import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi

/**
 * Invoice invalidation reason during dynamic checkout flow.
 */
/** @suppress */
@ProcessOutInternalApi
sealed class PODynamicCheckoutInvoiceInvalidationReason {
    /**
     * Indicates that user has selected different payment method in a state that requires new invoice.
     */
    data object PaymentMethodChanged : PODynamicCheckoutInvoiceInvalidationReason()

    /**
     * Indicates that selected method has failed and payment can't be continued with the current invoice.
     */
    data class Failure(
        val failure: ProcessOutResult.Failure
    ) : PODynamicCheckoutInvoiceInvalidationReason()
}
