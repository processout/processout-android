package com.processout.sdk.api.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Details of the card that should be tokenized.
 *
 * @param[number] Number of the card or Google Pay token.
 * @param[expMonth] Expiry month of the card.
 * @param[expYear] Expiry year of the card.
 * @param[cvc] Card Verification Code of the card.
 * @param[name] Name of the cardholder.
 * @param[contact] Information of cardholder.
 * @param[preferredScheme] Preferred scheme defined by the Customer.
 * @param[tokenType] Type of the token (e.g. Google Pay).
 * @param[paymentToken] Payment token.
 * @param[metadata] Metadata related to the card.
 */
data class POCardTokenizationRequest(
    val metadata: Map<String, String>? = null,
    val number: String? = "",
    val expMonth: Int? = 0,
    val expYear: Int? = 0,
    val cvc: String? = "",
    val name: String = "",
    val contact: POContact? = POContact(),
    val preferredScheme: String? = null,
    val tokenType: TokenType? = null,
    val paymentToken: String? = ""
) {
    /**
     * Supported token types.
     */
    @JsonClass(generateAdapter = false)
    enum class TokenType(val value: String) {
        /** Google Pay token type. */
        GOOGLE_PAY("googlepay")
    }
}

@JsonClass(generateAdapter = true)
internal data class CardTokenizationRequestWithDeviceData(
    val metadata: Map<String, String>? = emptyMap(),
    val number: String?,
    @Json(name = "exp_month")
    val expMonth: Int?,
    @Json(name = "exp_year")
    val expYear: Int?,
    val cvc: String?,
    val name: String,
    val contact: POContact?,
    @Json(name = "preferred_scheme")
    val preferredScheme: String?,
    @Json(name = "token_type")
    val tokenType: String?,
    @Json(name = "payment_token")
    val paymentToken: String?,
    @Json(name = "device")
    val deviceData: DeviceData? = null
)

/**
 * Cardholder information.
 *
 * @param[address1] First line of cardholder’s address.
 * @param[address2] Second line of cardholder’s address.
 * @param[city] City of cardholder’s address.
 * @param[state] State or county of cardholder’s address.
 * @param[zip] ZIP code of cardholder’s address.
 * @param[countryCode] Country code of cardholder’s address.
 */
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
