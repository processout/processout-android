package com.processout.sdk.ui.checkout

import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.event.PODynamicCheckoutEvent
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent
import com.processout.sdk.api.model.event.POSavedPaymentMethodsEvent
import com.processout.sdk.api.model.request.PODynamicCheckoutInvoiceInvalidationReason
import com.processout.sdk.api.model.request.POInvoiceAuthorizationRequest
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.api.model.response.POCardIssuerInformation
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod
import com.processout.sdk.api.model.response.POInvoice
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter
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

    /**
     * Allows to choose default preferred card scheme based on issuer information.
     * Primary card scheme is used by default.
     */
    fun preferredScheme(
        issuerInformation: POCardIssuerInformation
    ): String? = issuerInformation.scheme

    /**
     * Allows to prefill default values for the given parameters during native alternative payment.
     * Return a map where key is a [PONativeAlternativePaymentMethodParameter.key] and value is a custom default value.
     * It's not mandatory to provide default values for all parameters.
     */
    suspend fun defaultValues(
        paymentMethod: PODynamicCheckoutPaymentMethod.AlternativePayment,
        parameters: List<PONativeAlternativePaymentMethodParameter>
    ): Map<String, String> = emptyMap()

    /**
     * Allows to override default alternative payment configuration.
     * Invoked when payment method is about to start.
     * __Note:__ Changing _returnUrl_ is not supported and will throw _IllegalStateException_.
     */
    fun alternativePayment(
        paymentMethod: PODynamicCheckoutPaymentMethod.AlternativePayment,
        configuration: PODynamicCheckoutConfiguration.AlternativePaymentConfiguration
    ): PODynamicCheckoutConfiguration.AlternativePaymentConfiguration = configuration

    /**
     * Allows to customize saved payment methods configuration.
     */
    fun savedPaymentMethods(
        configuration: POSavedPaymentMethodsConfiguration
    ): POSavedPaymentMethodsConfiguration = configuration
}
