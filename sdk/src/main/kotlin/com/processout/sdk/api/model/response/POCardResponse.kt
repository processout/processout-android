package com.processout.sdk.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
internal data class POCardResponse(
    @Json(name = "card")
    val card: POCard
)

@JsonClass(generateAdapter = true)
data class POCard (
    // Value that uniquely identifies the card
    val id: String,

    // Project that the card belongs to
    @Json(name = "project_id")
    val projectId: String,

    // Scheme of the card
    val scheme: String?,

    // Co-scheme of the card, such as Carte Bancaire
    @Json(name = "co_scheme")
    val coScheme: String?,

    // Preferred scheme defined by the Customer
    @Json(name = "preferred_scheme")
    val preferredScheme: String?,

    // Card type
    val type: String?,

    // Name of the card’s issuing bank
    @Json(name = "bank_name")
    val bankName: String?,

    // Brand of the card
    val brand: String?,

    // Card category
    val category: String?,

    // Issuer identification number. Corresponds to the first 6 or 8 digits of the main card number.
    val iin: String?,

    // Last 4 digits of the card
    @Json(name = "last_4_digits")
    val last4Digits: String?,

    // Hash value that remains the same for this card even if it is tokenized several times
    val fingerprint: String?,

    // Month of the expiration date
    @Json(name = "exp_month")
    val expMonth: Int?,

    // Year of the expiration date
    @Json(name = "exp_year")
    val expYear: Int?,

    // CVC check status
    @Json(name = "cvc_check")
    val cvcCheck: String?,

    // AVS check status
    @Json(name = "avs_check")
    val avsCheck: String?,

    // Contains the name of a third party tokenization method
    @Json(name = "token_type")
    val tokenType: String?,

    // Cardholder’s name
    val name: String?,

    // First line of cardholder’s address
    val address1: String?,

    // Second line of cardholder’s address
    val address2: String?,

    // City of cardholder’s address
    val city: String?,

    // State or county of cardholder’s address
    val state: String?,

    // Country code of cardholder’s address
    @Json(name = "country_code")
    val countryCode: String?,

    // ZIP code of cardholder’s address
    val zip: String?,

    // Set to true if the card will expire soon, otherwise false
    @Json(name = "expires_soon")
    val expiresSoon: Boolean,

    // Metadata related to the card, in the form of key-value pairs
    val metadata: Map<String,String>?,

    // Denotes whether or not this card was created in the sandbox testing environment
    val sandbox: Boolean,

    // Date and time when this card was created
    @Json(name = "created_at")
    val createdAt: Date,

    @Json(name = "updated_at")
    val updatedAt: Date,

    // Type of card update
    @Json(name = "update_type")
    val updateType: String?,
)
