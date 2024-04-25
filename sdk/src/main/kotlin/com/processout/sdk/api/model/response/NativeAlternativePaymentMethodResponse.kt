package com.processout.sdk.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class NativeAlternativePaymentMethodResponse(
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
