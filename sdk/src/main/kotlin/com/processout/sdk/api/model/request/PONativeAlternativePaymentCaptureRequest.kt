package com.processout.sdk.api.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class PONativeAlternativePaymentCaptureRequest(
    @Json(name = "source")
    val gatewayConfigurationId: String
)
