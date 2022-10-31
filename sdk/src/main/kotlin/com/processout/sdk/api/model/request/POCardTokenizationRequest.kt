package com.processout.sdk.api.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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

open class POCardTokenizationRequest(
    // Metadata related to the card
    open val metadata: Map<String, String>? = emptyMap(),

    // Information about the card
    open val number: String? = "", // we can either have a card or a googlepay token
    open val expMonth: Int? = 0,
    open val expYear: Int? = 0,
    open val cvc: String? = "",
    open val name: String = "",
    open val contact: POContact? = POContact(),

    // Network Token specific fields
    open val tokenType: String? = "",
    open val paymentToken: String? = "",
) {
    enum class TokenType(val tokenType: String) {
        GOOGLE_PAY("googlepay"),
    }
}

@JsonClass(generateAdapter = true)
internal data class POCardTokenizationRequestWithDeviceData(
    override val metadata: Map<String, String>? = emptyMap(),
    override val number: String?,
    @Json(name = "exp_month")
    override val expMonth: Int?,
    @Json(name = "exp_year")
    override val expYear: Int?,
    override val cvc: String?,
    override val name: String,
    override val contact: POContact?,
    @Json(name = "token_type")
    override val tokenType: String?,
    @Json(name = "payment_token")
    override val paymentToken: String?,
    @Json(name = "device")
    val deviceData: PODeviceData? = null
) : POCardTokenizationRequest()
