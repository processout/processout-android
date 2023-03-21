package com.processout.sdk.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class POInvoiceAuthorizationResponse(
    @Json(name = "customer_action")
    val customerAction: POCustomerAction? = null
)
