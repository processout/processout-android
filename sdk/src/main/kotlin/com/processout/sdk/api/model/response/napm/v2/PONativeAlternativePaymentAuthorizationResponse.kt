package com.processout.sdk.api.model.response.napm.v2

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Specifies details of native alternative payment.
 *
 * @param[state] State of native alternative payment.
 * @param[paymentMethod] Payment method details.
 * @param[nextStep] Next required step in the payment flow.
 * @param[customerInstructions] Instructions for the customer that provide additional information and/or describe required actions.
 * @param[redirect] Indicates required redirect.
 */
/** @suppress */
@ProcessOutInternalApi
data class PONativeAlternativePaymentAuthorizationResponse(
    val state: PONativeAlternativePaymentState,
    val paymentMethod: PONativeAlternativePaymentMethodDetails?, // TODO(v2): non-nullable
    val nextStep: PONativeAlternativePaymentNextStep?,
    val customerInstructions: List<PONativeAlternativePaymentCustomerInstruction>?,
    val redirect: PONativeAlternativePaymentRedirect?
)

@JsonClass(generateAdapter = true)
internal data class NativeAlternativePaymentAuthorizationResponseBody(
    val state: PONativeAlternativePaymentState,
    @Json(name = "payment_method")
    val paymentMethod: PONativeAlternativePaymentMethodDetails?, // TODO(v2): non-nullable
    @Json(name = "next_step")
    val nextStep: NativeAlternativePaymentNextStep?,
    @Json(name = "customer_instructions")
    val customerInstructions: List<PONativeAlternativePaymentCustomerInstruction>?,
    val redirect: PONativeAlternativePaymentRedirect?
)
