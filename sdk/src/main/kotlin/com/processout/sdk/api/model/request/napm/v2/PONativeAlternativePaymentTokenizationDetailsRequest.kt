package com.processout.sdk.api.model.request.napm.v2

import com.processout.sdk.core.annotation.ProcessOutInternalApi

/**
 * Request parameters for native alternative payment tokenization details.
 *
 * @param[gatewayConfigurationId] Gateway configuration identifier.
 * @param[customerId] Customer identifier.
 * @param[customerTokenId] Customer token identifier.
 */
/** @suppress */
@ProcessOutInternalApi
data class PONativeAlternativePaymentTokenizationDetailsRequest(
    val gatewayConfigurationId: String,
    val customerId: String,
    val customerTokenId: String
)
