package com.processout.sdk.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class PONativeAlternativePaymentMethodTransactionDetailsResponse(
    @Json(name = "native_apm")
    val nativeApm: PONativeAlternativePaymentMethodTransactionDetails
)

@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentMethodTransactionDetails(
    val state: PONativeAlternativePaymentMethodState?,
    val gateway: Gateway,
    val invoice: Invoice,
    val parameters: List<PONativeAlternativePaymentMethodParameter>?
) {

    @JsonClass(generateAdapter = true)
    data class Gateway(
        @Json(name = "display_name")
        val displayName: String,
        @Json(name = "logo_url")
        val logoUrl: String,
        @Json(name = "customer_action_message")
        val customerActionMessage: String?,
        @Json(name = "customer_action_image_url")
        val customerActionImageUrl: String?
    )

    @JsonClass(generateAdapter = true)
    data class Invoice(
        val amount: String,
        @Json(name = "currency_code")
        val currencyCode: String
    )
}
