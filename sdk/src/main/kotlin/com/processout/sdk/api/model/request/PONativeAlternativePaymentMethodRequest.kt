package com.processout.sdk.api.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request parameters used to initiate payment.
 *
 * @param[invoiceId] Invoice identifier.
 * @param[gatewayConfigurationId] Gateway configuration identifier.
 * @param[parameters] Payment request parameters.
 */
data class PONativeAlternativePaymentMethodRequest(
    val invoiceId: String,
    val gatewayConfigurationId: String,
    val parameters: Map<String, String>
)

@JsonClass(generateAdapter = true)
internal data class PONativeAPMRequestBody(
    @Json(name = "gateway_configuration_id")
    val gatewayConfigurationId: String,
    @Json(name = "native_apm")
    val nativeApm: PONativeAPMRequestParameters
)

@JsonClass(generateAdapter = true)
internal data class PONativeAPMRequestParameters(
    @Json(name = "parameter_values")
    val parameterValues: Map<String, String>
)
