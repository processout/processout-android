package com.processout.sdk.api.model.response.napm.v2

import android.os.Parcelable
import com.processout.sdk.api.model.response.POImageResource
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * Specifies payment method details.
 *
 * @param[gatewayConfigurationId] Gateway configuration identifier.
 * @param[displayName] Payment method display name.
 * @param[logo] Image resource for light/dark themes.
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentMethodDetails(
    @Json(name = "gateway_configuration_id")
    val gatewayConfigurationId: String,
    @Json(name = "display_name")
    val displayName: String,
    val logo: POImageResource
) : Parcelable
