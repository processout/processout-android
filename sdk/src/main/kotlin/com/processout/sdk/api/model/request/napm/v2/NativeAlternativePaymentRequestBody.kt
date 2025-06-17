package com.processout.sdk.api.model.request.napm.v2

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class NativeAlternativePaymentRequestBody(
    @Json(name = "gateway_configuration_id")
    val gatewayConfigurationId: String,
    @Json(name = "submit_data")
    val submitData: SubmitData?
) {

    @JsonClass(generateAdapter = true)
    data class SubmitData(
        val parameters: Map<String, Any>
    )
}
