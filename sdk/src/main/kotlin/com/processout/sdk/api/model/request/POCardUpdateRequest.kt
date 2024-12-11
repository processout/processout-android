package com.processout.sdk.api.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request to update card information.
 *
 * @param[cardId] Unique identifier of the tokenized card.
 * @param[cvc] Card Verification Code. Pass _null_ to keep existing value.
 * @param[preferredScheme] Preferred scheme defined by the Customer.
 * This gets priority when processing the Transaction.
 * If you wish to update this back to empty, you can use the value _none_.
 * Pass _null_ to keep existing value.
 */
data class POCardUpdateRequest(
    val cardId: String,
    val cvc: String? = null,
    val preferredScheme: String? = null
)

@JsonClass(generateAdapter = true)
internal data class CardUpdateRequestBody(
    val cvc: String?,
    @Json(name = "preferred_scheme")
    val preferredScheme: String?
)
