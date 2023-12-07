package com.processout.sdk.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class CaptureResponse(
    @Json(name = "native_apm")
    val nativeApm: PONativeAlternativePaymentMethodCapture
)

/**
 * Defines capture state.
 *
 * @param[state] State of native alternative payment.
 */
@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentMethodCapture(
    val state: PONativeAlternativePaymentMethodState
)
