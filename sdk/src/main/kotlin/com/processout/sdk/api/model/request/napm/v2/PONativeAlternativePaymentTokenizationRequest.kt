package com.processout.sdk.api.model.request.napm.v2

/**
 * Request parameters for native alternative payment tokenization.
 *
 * @param[customerId] Customer identifier.
 * @param[customerTokenId] Customer token identifier.
 * @param[gatewayConfigurationId] Gateway configuration identifier.
 * @param[configuration] Payment configuration.
 * __Note:__ Configuration is respected _only on the first_ payment request and ignored on subsequent calls.
 * @param[submitData] Payment payload.
 * @param[redirectConfirmation] Redirect confirmation.
 */
data class PONativeAlternativePaymentTokenizationRequest(
    val customerId: String,
    val customerTokenId: String,
    val gatewayConfigurationId: String,
    val configuration: PONativeAlternativePaymentRequestConfiguration = PONativeAlternativePaymentRequestConfiguration(),
    val submitData: PONativeAlternativePaymentSubmitData? = null,
    val redirectConfirmation: PONativeAlternativePaymentRedirectConfirmation? = null
)
