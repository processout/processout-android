package com.processout.sdk.api.model.response

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class InvoiceResponse(
    val invoice: POInvoice
)

/** @suppress */
@ProcessOutInternalApi
@JsonClass(generateAdapter = true)
data class POInvoice(
    val id: String
)
