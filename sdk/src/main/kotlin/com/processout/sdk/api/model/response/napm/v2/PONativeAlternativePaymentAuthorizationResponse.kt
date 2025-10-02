package com.processout.sdk.api.model.response.napm.v2

import android.os.Parcelable
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse.Invoice
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * Specifies details of native alternative payment.
 *
 * @param[state] State of native alternative payment.
 * @param[invoice] Invoice details.
 * @param[paymentMethod] Payment method details.
 * @param[elements] An ordered list of elements that needs to be rendered on the UI during native alternative payment flow.
 * @param[redirect] Indicates required redirect.
 */
@Parcelize
data class PONativeAlternativePaymentAuthorizationResponse(
    val state: PONativeAlternativePaymentState,
    val invoice: Invoice,
    val paymentMethod: PONativeAlternativePaymentMethodDetails,
    val elements: List<PONativeAlternativePaymentElement>?,
    val redirect: PONativeAlternativePaymentRedirect?
) : Parcelable {

    /**
     * Invoice details.
     *
     * @param[amount] Invoice amount.
     * @param[currency] Invoice currency.
     */
    @Parcelize
    @JsonClass(generateAdapter = true)
    data class Invoice(
        val amount: String,
        val currency: String
    ) : Parcelable
}

@JsonClass(generateAdapter = true)
internal data class NativeAlternativePaymentAuthorizationResponseBody(
    val state: PONativeAlternativePaymentState,
    val invoice: Invoice,
    @Json(name = "payment_method")
    val paymentMethod: PONativeAlternativePaymentMethodDetails,
    val elements: List<NativeAlternativePaymentElement>?,
    val redirect: PONativeAlternativePaymentRedirect?
)
