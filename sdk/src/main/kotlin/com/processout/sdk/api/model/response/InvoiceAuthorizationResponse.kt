package com.processout.sdk.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class InvoiceAuthorizationResponse(
    @Json(name = "customer_action")
    val customerAction: CustomerAction?
)
