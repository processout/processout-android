package com.processout.sdk.api.model.response

import com.squareup.moshi.JsonClass

/**
 * Invoice details.
 *
 * @param[id] Invoice identifier.
 */
@JsonClass(generateAdapter = true)
data class POInvoice(
    val id: String
)

@JsonClass(generateAdapter = true)
internal data class CreateInvoiceResponse(
    val invoice: POInvoice
)
