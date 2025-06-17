package com.processout.sdk.api.model.request.napm.v2

import com.processout.sdk.core.annotation.ProcessOutInternalApi

/**
 * Request parameters for native alternative payment tokenization.
 *
 * @param[customerId] Customer identifier.
 * @param[customerTokenId] Customer token identifier.
 * @param[gatewayConfigurationId] Gateway configuration identifier.
 * @param[submitData] Payment payload.
 */
/** @suppress */
@ProcessOutInternalApi
data class PONativeAlternativePaymentTokenizationRequest(
    val customerId: String,
    val customerTokenId: String,
    val gatewayConfigurationId: String,
    val submitData: PONativeAlternativePaymentSubmitData? = null
)
