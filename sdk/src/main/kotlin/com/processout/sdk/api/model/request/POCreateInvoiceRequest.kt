package com.processout.sdk.api.model.request

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.JsonClass

@ProcessOutInternalApi
@JsonClass(generateAdapter = true)
data class POCreateInvoiceRequest(
    val name: String,
    val amount: String,
    val currency: String,
    val device: Map<String, String> = mapOf("channel" to "android")
)
