package com.processout.sdk.api.model.response.napm.v2

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Specifies details of native alternative payment after redirect URL resolution.
 *
 * @param[state] State of native alternative payment.
 * @param[paymentMethod] Payment method details.
 * @param[invoice] Invoice details if any.
 * @param[customerToken] Customer token details if any.
 * @param[elements] An ordered list of elements that needs to be rendered on the UI during native alternative payment flow.
 * @param[redirect] Indicates required redirect.
 */
@ProcessOutInternalApi
data class PONativeAlternativePaymentUrlResolutionResponse(
    val state: PONativeAlternativePaymentState,
    val paymentMethod: PONativeAlternativePaymentMethodDetails,
    val invoice: PONativeAlternativePaymentInvoice?,
    val customerToken: PONativeAlternativePaymentCustomerToken?,
    val elements: List<PONativeAlternativePaymentElement>?,
    val redirect: PONativeAlternativePaymentRedirect?
)

@JsonClass(generateAdapter = true)
internal data class NativeAlternativePaymentUrlResolutionResponseBody(
    val state: PONativeAlternativePaymentState,
    @Json(name = "payment_method")
    val paymentMethod: PONativeAlternativePaymentMethodDetails,
    val invoice: PONativeAlternativePaymentInvoice?,
    @Json(name = "customer_token")
    val customerToken: PONativeAlternativePaymentCustomerToken?,
    val elements: List<NativeAlternativePaymentElement>?,
    val redirect: PONativeAlternativePaymentRedirect?
)
