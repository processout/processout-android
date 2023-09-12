package com.processout.sdk.api.model.request

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** @suppress */
@ProcessOutInternalApi
@JsonClass(generateAdapter = true)
data class POCreateInvoiceRequest(
    val name: String,
    val amount: String,
    val currency: String,
    @Json(name = "customer_id")
    val customerId: String? = null,
    @Json(name = "statement_descriptor")
    val statementDescriptor: String? = null,
    @Json(name = "return_url")
    val returnUrl: String? = null,
    val device: Map<String, String> = mapOf("channel" to "android")
)
