package com.processout.sdk.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Invoice details.
 *
 * @param[id] Invoice identifier.
 * @param[amount] Invoice amount.
 * @param[currency] Invoice currency.
 * @param[returnUrl] Return URL or deep link for web based operations.
 */
@JsonClass(generateAdapter = true)
data class POInvoice(
    val id: String,
    val amount: String,
    val currency: String,
    @Json(name = "return_url")
    val returnUrl: String?
)

@JsonClass(generateAdapter = true)
internal data class CreateInvoiceResponse(
    val invoice: POInvoice
)
