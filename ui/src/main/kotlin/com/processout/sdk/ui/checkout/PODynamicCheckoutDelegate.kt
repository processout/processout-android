package com.processout.sdk.ui.checkout

import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.event.PODynamicCheckoutEvent
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent
import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod
import com.processout.sdk.api.model.response.POInvoice
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
interface PODynamicCheckoutDelegate {

    fun onEvent(event: PODynamicCheckoutEvent) {}

    fun onEvent(event: POCardTokenizationEvent) {}

    fun onEvent(event: PONativeAlternativePaymentMethodEvent) {}

    /**
     * __Note:__ please make sure to invalidate current invoice before creating the new one.
     */
    suspend fun newInvoice(
        currentInvoice: POInvoice,
        invalidationReason: PODynamicCheckoutInvoiceInvalidationReason
    ): POInvoiceRequest? = null

    /**
     * Called before invoice authorization.
     * Allows to alter request parameters but please make sure that _invoiceId_ and _source_ are unmodified.
     */
    suspend fun invoiceAuthorizationRequest(
        request: POInvoiceAuthorizationRequest,
        paymentMethod: PODynamicCheckoutPaymentMethod
    ): POInvoiceAuthorizationRequest = request

    suspend fun preferredScheme(
        request: POCardTokenizationPreferredSchemeRequest
    ): String? = null

    suspend fun defaultValues(
        request: PONativeAlternativePaymentMethodDefaultValuesRequest
    ): Map<String, String> = emptyMap()
}
