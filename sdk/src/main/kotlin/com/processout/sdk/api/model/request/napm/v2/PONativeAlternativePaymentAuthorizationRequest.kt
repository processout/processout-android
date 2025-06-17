package com.processout.sdk.api.model.request.napm.v2

import com.processout.sdk.core.annotation.ProcessOutInternalApi

/**
 * Request parameters for native alternative payment authorization.
 *
 * @param[invoiceId] Invoice identifier.
 * @param[gatewayConfigurationId] Gateway configuration identifier.
 * @param[submitData] Payment payload.
 */
/** @suppress */
@ProcessOutInternalApi
data class PONativeAlternativePaymentAuthorizationRequest(
    val invoiceId: String,
    val gatewayConfigurationId: String,
    val submitData: PONativeAlternativePaymentSubmitData? = null
)
