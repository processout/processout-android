package com.processout.sdk.api.model.request

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** @suppress */
@ProcessOutInternalApi
data class POCreateCustomerTokenRequest(
    val customerId: String,
    val body: POCreateCustomerTokenRequestBody
)

/** @suppress */
@ProcessOutInternalApi
@JsonClass(generateAdapter = true)
data class POCreateCustomerTokenRequestBody(
    val verify: Boolean = false,
    @Json(name = "invoice_return_url")
    val returnUrl: String? = null
)
