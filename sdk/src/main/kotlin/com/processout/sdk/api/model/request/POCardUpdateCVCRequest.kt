package com.processout.sdk.api.model.request

import com.squareup.moshi.JsonClass

/**
 * Request to update Card Verification Code of the card.
 *
 * @param[cvc] Card Verification Code of the card.
 */
@JsonClass(generateAdapter = true)
data class POCardUpdateCVCRequest(
    val cvc: String,
)
