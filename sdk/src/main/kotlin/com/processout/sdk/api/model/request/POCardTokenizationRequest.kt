package com.processout.sdk.api.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class POCardTokenizationRequest(
    // Metadata related to the card
    val metadata: Map<String, String>? = emptyMap(),

    // Information about the card
    val number: String? = "", // we can either have a card or a googlepay token
    val expMonth: Int? = 0,
    val expYear: Int? = 0,
    val cvc: String? = "",
    val name: String = "",
    val contact: POContact? = POContact(),

    // Network Token specific fields
    val tokenType: TokenType? = null,
    val paymentToken: String? = ""
) {
    enum class TokenType(val value: String) {
        GOOGLE_PAY("googlepay")
    }
}

@JsonClass(generateAdapter = true)
internal data class POCardTokenizationRequestWithDeviceData(
    val metadata: Map<String, String>? = emptyMap(),
    val number: String?,
    @Json(name = "exp_month")
    val expMonth: Int?,
    @Json(name = "exp_year")
    val expYear: Int?,
    val cvc: String?,
    val name: String,
    val contact: POContact?,
    @Json(name = "token_type")
    val tokenType: String?,
    @Json(name = "payment_token")
    val paymentToken: String?,
    @Json(name = "device")
    val deviceData: PODeviceData? = null
)

@JsonClass(generateAdapter = true)
data class POContact(
    val address1: String? = "",
    val address2: String? = "",
    val city: String? = "",
    val state: String? = "",
    val zip: String? = "",
    @Json(name = "country_code")
    val countryCode: String? = ""
)
