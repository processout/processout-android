package com.processout.sdk.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Native alternative payment parameter values.
 *
 * @param[message] Message.
 * @param[customerActionMessage] Customer action message markdown that should be used to explain user how to proceed with payment.
 * Currently it will be set only when payment state is PENDING_CAPTURE.
 * @param[providerName] Payment provider name.
 * @param[providerLogoUrl] Payment provider logo URL if available.
 */
@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentMethodParameterValues(
    val message: String?,
    @Json(name = "customer_action_message")
    val customerActionMessage: String?,
    @Json(name = "provider_name")
    val providerName: String?,
    @Json(name = "provider_logo_url")
    val providerLogoUrl: String?
)
