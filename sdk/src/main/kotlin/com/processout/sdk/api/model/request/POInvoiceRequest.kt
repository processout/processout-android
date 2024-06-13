package com.processout.sdk.api.model.request

import com.squareup.moshi.JsonClass

/**
 * Request to get single invoice details.
 *
 * @param[id] Invoice identifier.
 */
@JsonClass(generateAdapter = true)
data class POInvoiceRequest(
    val id: String
)
