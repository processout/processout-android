package com.processout.sdk.api.model.response

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class POInvoiceResponse(
    val invoice: POInvoice
)

@ProcessOutInternalApi
@JsonClass(generateAdapter = true)
data class POInvoice(
    val id: String
)
