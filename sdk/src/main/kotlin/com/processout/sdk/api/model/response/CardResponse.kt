package com.processout.sdk.api.model.response

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.Date

@JsonClass(generateAdapter = true)
internal data class CardResponse(
    @Json(name = "card")
    val card: POCard
)

/**
 * A card object represents a credit or debit card.
 * It contains many useful pieces of information about the card
 * but it does not contain the full card number and CVC
 * which are kept securely in the ProcessOut Vault.
 *
 * @param[id] Value that uniquely identifies the card.
 * @param[projectId] Project that the card belongs to.
 * @param[scheme] Scheme of the card.
 * @param[coScheme] Co-scheme of the card, such as Carte Bancaire.
 * @param[preferredScheme] Preferred scheme defined by the Customer.
 * @param[type] Card type.
 * @param[bankName] Name of the card’s issuing bank.
 * @param[brand] Brand of the card.
 * @param[category] Card category.
 * @param[iin] Issuer Identification Number. Corresponds to the first 6 or 8 digits of the main card number.
 * @param[last4Digits] Last 4 digits of the card.
 * @param[fingerprint] Hash value that remains the same for this card even if it is tokenized several times.
 * @param[expMonth] Month of the expiration date.
 * @param[expYear] Year of the expiration date.
 * @param[cvcCheck] CVC check status.
 * @param[avsCheck] AVS check status.
 * @param[tokenType] Contains the name of a third party tokenization method.
 * @param[name] Cardholder’s name.
 * @param[address1] First line of cardholder’s address.
 * @param[address2] Second line of cardholder’s address.
 * @param[city] City of cardholder’s address.
 * @param[state] State or county of cardholder’s address.
 * @param[countryCode] Country code of cardholder’s address.
 * @param[zip] ZIP code of cardholder’s address.
 * @param[expiresSoon] Set to true if the card will expire soon, otherwise false.
 * @param[metadata] Metadata related to the card, in the form of key-value pairs.
 * @param[sandbox] Denotes whether or not this card was created in the sandbox testing environment.
 * @param[createdAt] Date and time when this card was created.
 * @param[updatedAt] Date and time when this card was updated.
 * @param[updateType] Type of card update.
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class POCard(
    val id: String,
    @Json(name = "project_id")
    val projectId: String,
    val scheme: String?,
    @Json(name = "co_scheme")
    val coScheme: String?,
    @Json(name = "preferred_scheme")
    val preferredScheme: String?,
    val type: String?,
    @Json(name = "bank_name")
    val bankName: String?,
    val brand: String?,
    val category: String?,
    val iin: String?,
    @Json(name = "last_4_digits")
    val last4Digits: String?,
    val fingerprint: String?,
    @Json(name = "exp_month")
    val expMonth: Int?,
    @Json(name = "exp_year")
    val expYear: Int?,
    @Json(name = "cvc_check")
    val cvcCheck: String?,
    @Json(name = "avs_check")
    val avsCheck: String?,
    @Json(name = "token_type")
    val tokenType: String?,
    val name: String?,
    val address1: String?,
    val address2: String?,
    val city: String?,
    val state: String?,
    @Json(name = "country_code")
    val countryCode: String?,
    val zip: String?,
    @Json(name = "expires_soon")
    val expiresSoon: Boolean,
    val metadata: Map<String, String>?,
    val sandbox: Boolean,
    @Json(name = "created_at")
    val createdAt: Date,
    @Json(name = "updated_at")
    val updatedAt: Date,
    @Json(name = "update_type")
    val updateType: String?
) : Parcelable
