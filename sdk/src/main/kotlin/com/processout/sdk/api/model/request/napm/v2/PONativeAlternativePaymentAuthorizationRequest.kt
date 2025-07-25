package com.processout.sdk.api.model.request.napm.v2

/**
 * Request parameters for native alternative payment authorization.
 *
 * @param[invoiceId] Invoice identifier.
 * @param[gatewayConfigurationId] Gateway configuration identifier.
 * @param[source] Payment source.
 * @param[submitData] Payment payload.
 */
data class PONativeAlternativePaymentAuthorizationRequest(
    val invoiceId: String,
    val gatewayConfigurationId: String,
    val source: String? = null,
    val submitData: PONativeAlternativePaymentSubmitData? = null
)
