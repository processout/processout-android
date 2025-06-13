package com.processout.sdk.api.model.response.napm.v2

import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse.Invoice
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Specifies details of native alternative payment.
 *
 * @param[state] State of native alternative payment.
 * @param[invoice] Invoice details.
 * @param[paymentMethod] Payment method details.
 * @param[nextStep] Next required step in the payment flow.
 * @param[customerInstructions] Instructions for the customer that provide additional information and/or describe required actions.
 * @param[redirect] Indicates required redirect.
 */
/** @suppress */
@ProcessOutInternalApi
data class PONativeAlternativePaymentAuthorizationResponse(
    val state: PONativeAlternativePaymentState,
    val invoice: Invoice?, // TODO(v2): non-nullable
    val paymentMethod: PONativeAlternativePaymentMethodDetails?, // TODO(v2): non-nullable
    val elements: List<PONativeAlternativePaymentElement>?,
    val redirect: PONativeAlternativePaymentRedirect?
) {

    /**
     * Invoice details.
     *
     * @param[amount] Invoice amount.
     * @param[currency] Invoice currency.
     */
    @JsonClass(generateAdapter = true)
    data class Invoice(
        val amount: String,
        val currency: String
    )
}

@JsonClass(generateAdapter = true)
internal data class NativeAlternativePaymentAuthorizationResponseBody(
    val state: PONativeAlternativePaymentState,
    val invoice: Invoice?, // TODO(v2): non-nullable
    @Json(name = "payment_method")
    val paymentMethod: PONativeAlternativePaymentMethodDetails?, // TODO(v2): non-nullable
    val elements: List<NativeAlternativePaymentElement>?,
    val redirect: PONativeAlternativePaymentRedirect?
)
