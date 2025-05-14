package com.processout.sdk.api.model.request.napm.v2

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class NativeAlternativePaymentAuthorizationRequestBody(
    val gatewayConfigurationId: String
)
