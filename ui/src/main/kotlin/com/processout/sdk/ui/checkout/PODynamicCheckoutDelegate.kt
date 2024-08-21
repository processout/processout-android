package com.processout.sdk.ui.checkout

import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent
import com.processout.sdk.api.model.request.PODynamicCheckoutInvoiceInvalidationReason
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.api.model.response.POInvoice
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
interface PODynamicCheckoutDelegate {

    fun onEvent(event: POCardTokenizationEvent) {}

    fun onEvent(event: PONativeAlternativePaymentMethodEvent) {}

    /**
     * __Note:__ please make sure to invalidate current invoice before creating the new one.
     */
    suspend fun newInvoice(
        currentInvoice: POInvoice,
        invalidationReason: PODynamicCheckoutInvoiceInvalidationReason
    ): POInvoiceRequest? = null
}
