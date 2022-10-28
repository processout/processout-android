package com.processout.sdk.api.model.request

import androidx.annotation.RestrictTo
import com.squareup.moshi.JsonClass

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@JsonClass(generateAdapter = true)
data class POCreateInvoiceRequest(
    val name: String,
    val amount: String,
    val currency: String,
    val device: Map<String, String> = mapOf("channel" to "android")
)
