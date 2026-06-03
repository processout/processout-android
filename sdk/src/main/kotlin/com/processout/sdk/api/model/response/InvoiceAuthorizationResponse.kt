package com.processout.sdk.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class InvoiceAuthorizationResponse(
    @Json(name = "customer_action")
    val customerAction: CustomerAction?,
    @Json(name = "customer_token_id")
    val customerTokenId: String?
)

/**
 * Invoice authorization response.
 *
 * @param[customerTokenId] Optional customer token ID.
 * Available if invoice was authorized with `saveSource` flag set to `true` and implementation was able to tokenize the source.
 */
data class POInvoiceAuthorizationResponse(
    val customerTokenId: String?
)
