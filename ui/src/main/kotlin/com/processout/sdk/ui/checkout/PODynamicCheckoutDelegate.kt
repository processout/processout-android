package com.processout.sdk.ui.checkout

import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.event.PODynamicCheckoutEvent
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent
import com.processout.sdk.api.model.event.POSavedPaymentMethodsEvent
import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod
import com.processout.sdk.api.model.response.POInvoice
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.savedpaymentmethods.POSavedPaymentMethodsConfiguration

/**
 * Delegate that allows to handle events during dynamic checkout flow.
 */
/** @suppress */
@ProcessOutInternalApi
interface PODynamicCheckoutDelegate {

    /**
     * Invoked on dynamic checkout lifecycle events.
     */
    fun onEvent(event: PODynamicCheckoutEvent) {}

    /**
     * Invoked on card tokenization lifecycle events.
     */
    fun onEvent(event: POCardTokenizationEvent) {}

    /**
     * Invoked on native alternative payment lifecycle events.
     */
    fun onEvent(event: PONativeAlternativePaymentMethodEvent) {}

    /**
     * Invoked on saved payment methods lifecycle events.
     */
    fun onEvent(event: POSavedPaymentMethodsEvent) {}

    /**
     * Invoked in a state when payment can't be continued with the current invoice.
     * Create new invoice and return [POInvoiceRequest] to recover the flow.
     * Return _null_ to complete the flow with the failure.
     * __Note:__ Please make sure to invalidate the current invoice before creating a new one.
     */
    suspend fun newInvoice(
        currentInvoice: POInvoice,
        invalidationReason: PODynamicCheckoutInvoiceInvalidationReason
    ): POInvoiceRequest? = null

    /**
     * Invoked before invoice authorization.
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

    suspend fun savedPaymentMethods(
        configuration: POSavedPaymentMethodsConfiguration
    ): POSavedPaymentMethodsConfiguration = configuration
}
