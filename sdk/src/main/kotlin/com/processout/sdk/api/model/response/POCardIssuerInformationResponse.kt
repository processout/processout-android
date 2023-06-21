package com.processout.sdk.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class POCardIssuerInformationResponse(
    @Json(name = "card_information")
    val cardInformation: POCardIssuerInformation
)

@JsonClass(generateAdapter = true)
data class POCardIssuerInformation(
    val scheme: String,
    @Json(name = "co_scheme")
    val coScheme: String?,
    val type: String?,
    @Json(name = "bank_name")
    val bankName: String?,
    val brand: String?,
    val category: String?,
    val country: String?
)
