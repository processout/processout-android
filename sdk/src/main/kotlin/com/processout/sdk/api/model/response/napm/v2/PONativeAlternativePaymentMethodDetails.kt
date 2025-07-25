package com.processout.sdk.api.model.response.napm.v2

import com.processout.sdk.api.model.response.POImageResource
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Specifies payment method details.
 *
 * @param[displayName] Payment method display name.
 * @param[logo] Image resource for light/dark themes.
 */
@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentMethodDetails(
    @Json(name = "display_name")
    val displayName: String,
    val logo: POImageResource
)
