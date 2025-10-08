package com.processout.sdk.api.model.response.napm.v2

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * Specifies details of native alternative payment.
 *
 * @param[state] State of native alternative payment.
 * @param[paymentMethod] Payment method details.
 * @param[elements] An ordered list of elements that needs to be rendered on the UI during native alternative payment flow.
 * @param[redirect] Indicates required redirect.
 */
@Parcelize
data class PONativeAlternativePaymentTokenizationResponse(
    val state: PONativeAlternativePaymentState,
    val paymentMethod: PONativeAlternativePaymentMethodDetails,
    val elements: List<PONativeAlternativePaymentElement>?,
    val redirect: PONativeAlternativePaymentRedirect?
) : Parcelable

@JsonClass(generateAdapter = true)
internal data class NativeAlternativePaymentTokenizationResponseBody(
    val state: PONativeAlternativePaymentState,
    @Json(name = "payment_method")
    val paymentMethod: PONativeAlternativePaymentMethodDetails,
    val elements: List<NativeAlternativePaymentElement>?,
    val redirect: PONativeAlternativePaymentRedirect?
)
