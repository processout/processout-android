package com.processout.sdk.api.model.request.napm.v2

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.JsonClass

/** @suppress */
@ProcessOutInternalApi
data class PONativeAlternativePaymentAuthorizationRequest(
    val invoiceId: String,
    val gatewayConfigurationId: String
)

@JsonClass(generateAdapter = true)
internal data class NativeAlternativePaymentAuthorizationRequestBody(
    val gatewayConfigurationId: String
)
