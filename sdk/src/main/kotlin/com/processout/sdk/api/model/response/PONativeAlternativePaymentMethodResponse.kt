package com.processout.sdk.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class PONativeAlternativePaymentMethodResponse(
    @Json(name = "native_apm")
    val nativeApm: PONativeAlternativePaymentMethod
)

@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentMethod(
    val state: State,
    val parameterDefinitions: List<PONativeAlternativePaymentMethodParameter>?,
    val parameterValues: PONativeAlternativePaymentMethodParameterValues?
) {
    enum class State {
        CUSTOMER_INPUT,
        PENDING_CAPTURE
    }
}

@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentMethodParameterValues(
    val message: String?
)
