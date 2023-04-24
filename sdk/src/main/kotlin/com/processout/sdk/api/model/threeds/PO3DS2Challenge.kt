package com.processout.sdk.api.model.threeds

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Information from the 3DS Server authentication response that could be used by the 3DS2 SDK to initiate the challenge flow.
 *
 * @param acsTransactionId Unique transaction identifier assigned by the ACS.
 * @param acsReferenceNumber Unique identifier that identifies the ACS service provider.
 * @param acsSignedContent The encrypted message containing the ACS information (including Ephemeral Public Key) and the 3DS2 SDK ephemeral public key.
 * @param threeDSServerTransactionId Unique identifier for the authentication assigned by the DS (Card Scheme).
 */
@JsonClass(generateAdapter = true)
data class PO3DS2Challenge(
    @Json(name = "acsTransID")
    val acsTransactionId: String,
    val acsReferenceNumber: String,
    val acsSignedContent: String,
    @Json(name = "threeDSServerTransID")
    val threeDSServerTransactionId: String
)
