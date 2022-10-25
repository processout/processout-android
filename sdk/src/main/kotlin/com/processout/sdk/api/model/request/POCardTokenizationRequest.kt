package com.processout.sdk.api.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Contact (
    val address1: String? = "",
    val address2: String? = "",
    val city: String? = "",
    val state: String? = "",
    val zip: String? = "",
    @Json(name = "country_code")
    val countryCode: String? = ""
)

@JsonClass(generateAdapter = true)
data class POCardTokenizationRequest (
    // Metadata related to the card
    val metadata: Map<String, String>? = emptyMap(),

    // Information about the card
    val number: String? = "", // we can either have a card or a googlepay token
    @Json(name = "exp_month")
    val expMonth: Int? = 0,
    @Json(name = "exp_year")
    val expYear: Int? = 0,
    val cvc: String? = "",
    val name: String = "",
    val contact: Contact? = Contact(),

    // Network Token specific fields
    @Json(name = "token_type")
    val tokenType:String? = "",
    @Json(name = "payment_token")
    val paymentToken:String? = "",

) {
    enum class TokenType(val tokenType: String) {
        GOOGLE_PAY("googlepay"),
    }
}


