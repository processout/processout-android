package com.processout.sdk.api.model.request.napm.v2

import com.processout.sdk.core.annotation.ProcessOutInternalApi

/**
 * Request parameters for native alternative payment.
 *
 * @param[invoiceId] Invoice identifier.
 * @param[gatewayConfigurationId] Gateway configuration identifier.
 */
/** @suppress */
@ProcessOutInternalApi
data class PONativeAlternativePaymentRequest(
    val invoiceId: String,
    val gatewayConfigurationId: String
)
