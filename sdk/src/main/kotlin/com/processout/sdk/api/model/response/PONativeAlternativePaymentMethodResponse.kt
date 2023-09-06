package com.processout.sdk.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class PONativeAlternativePaymentMethodResponse(
    @Json(name = "native_apm")
    val nativeApm: PONativeAlternativePaymentMethod
)

/**
 * Details of native alternative payment method.
 *
 * @param[state] Current state of payment.
 * @param[parameterDefinitions] Contains details about the additional information you need to collect from customer before creating the payment request.
 * @param[parameterValues] Additional information about the payment step.
 */
@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentMethod(
    val state: PONativeAlternativePaymentMethodState,
    val parameterDefinitions: List<PONativeAlternativePaymentMethodParameter>?,
    val parameterValues: PONativeAlternativePaymentMethodParameterValues?
)

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
