package com.processout.sdk.api.model.request.napm.v2

/**
 * Request parameters for native alternative payment authorization.
 *
 * @param[invoiceId] Invoice identifier.
 * @param[gatewayConfigurationId] Gateway configuration identifier.
 * @param[configuration] Payment configuration.
 * __Note:__ Configuration is respected _only on the first_ payment request and ignored on subsequent calls.
 * @param[source] Payment source.
 * @param[submitData] Payment payload.
 * @param[redirectConfirmation] Redirect confirmation.
 */
data class PONativeAlternativePaymentAuthorizationRequest(
    val invoiceId: String,
    val gatewayConfigurationId: String,
    val configuration: PONativeAlternativePaymentRequestConfiguration = PONativeAlternativePaymentRequestConfiguration(),
    val source: String? = null,
    val submitData: PONativeAlternativePaymentSubmitData? = null,
    val redirectConfirmation: PONativeAlternativePaymentRedirectConfirmation? = null
)
