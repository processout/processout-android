package com.processout.sdk.api.model.response

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
internal data class CardIssuerInformationResponse(
    @Json(name = "card_information")
    val cardInformation: POCardIssuerInformation
)

/**
 * Holds information about card issuing institution that issued the card to the card holder.
 *
 * @param[scheme] Scheme of the card.
 * @param[coScheme] Co-scheme of the card, such as Carte Bancaire.
 * @param[type] Card type.
 * @param[bankName] Name of the card’s issuing bank.
 * @param[brand] Brand of the card.
 * @param[category] Card category.
 * @param[country] Country of origin.
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class POCardIssuerInformation(
    val scheme: String,
    @Json(name = "co_scheme")
    val coScheme: String? = null,
    val type: String? = null,
    @Json(name = "bank_name")
    val bankName: String? = null,
    val brand: String? = null,
    val category: String? = null,
    val country: String? = null
) : Parcelable
