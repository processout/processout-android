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
    val state: PONativeAlternativePaymentMethodState,
    val parameterDefinitions: List<PONativeAlternativePaymentMethodParameter>?,
    val parameterValues: PONativeAlternativePaymentMethodParameterValues?
)

@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentMethodParameterValues(
    val message: String?,
    @Json(name = "customer_action_message")
    val customerActionMessage: String?,
    @Json(name = "provider_name")
    val providerName: String?,
    @Json(name = "provider_logo")
    val providerLogoUrl: String?
)
