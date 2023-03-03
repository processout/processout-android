package com.processout.sdk.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class POCaptureResponse(
    @Json(name = "native_apm")
    val nativeApm: PONativeAlternativePaymentMethodCapture
)

@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentMethodCapture(
    val state: PONativeAlternativePaymentMethodState
)
