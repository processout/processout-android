package com.processout.sdk.api.model.request

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@ProcessOutInternalApi
@JsonClass(generateAdapter = true)
data class POCreateCustomerRequest(
    @Json(name = "first_name")
    val firstName: String? = null,
    @Json(name = "last_name")
    val lastName: String? = null,
    @Json(name = "phone_number")
    val phoneNumber: String? = null,
    val address1: String? = null,
    val address2: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zip: String? = null,
    @Json(name = "country_code")
    val countryCode: String? = null,
    @Json(name = "legal_document")
    val legalDocument: String? = null,
    val sex: String? = null,
    val email: String? = null,
)
