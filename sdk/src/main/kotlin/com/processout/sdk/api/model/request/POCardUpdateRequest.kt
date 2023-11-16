package com.processout.sdk.api.model.request

import com.squareup.moshi.JsonClass

/**
 * Request to update card information.
 *
 * @param[cardId] Unique identifier of the card.
 * @param[cvc] Card Verification Code.
 */
data class POCardUpdateRequest(
    val cardId: String,
    val cvc: String
)

@JsonClass(generateAdapter = true)
internal data class CardUpdateRequestBody(
    val cvc: String
)
