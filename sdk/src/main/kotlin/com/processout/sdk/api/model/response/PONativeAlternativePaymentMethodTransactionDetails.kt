package com.processout.sdk.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class NativeAlternativePaymentMethodTransactionDetailsResponse(
    @Json(name = "native_apm")
    val nativeApm: PONativeAlternativePaymentMethodTransactionDetails
)

/**
 * Transaction details of native alternative payment method.
 *
 * @param[state] Current state of payment.
 * @param[gateway] Payment gateway information.
 * @param[invoice] Invoice details.
 * @param[parameters] Parameters that are expected from user.
 */
@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentMethodTransactionDetails(
    val state: PONativeAlternativePaymentMethodState?,
    val gateway: Gateway,
    val invoice: Invoice,
    val parameters: List<PONativeAlternativePaymentMethodParameter>?,
    @Json(name = "parameter_values")
    val parameterValues: PONativeAlternativePaymentMethodParameterValues?
) {

    /**
     * Payment gateway information.
     *
     * @param[displayName] Name of the payment gateway that can be displayed.
     * @param[logoUrl] Gateway’s logo URL.
     * @param[customerActionMessage] Customer action message markdown.
     * Before using this property check that [PONativeAlternativePaymentMethodParameterValues.customerActionMessage] is not set, otherwise use it instead.
     * @param[customerActionImageUrl] Customer action image URL if any.
     */
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

    /**
     * Invoice details.
     *
     * @param[amount] Invoice amount.
     * @param[currencyCode] Invoice currency code.
     */
    @JsonClass(generateAdapter = true)
    data class Invoice(
        val amount: String,
        @Json(name = "currency_code")
        val currencyCode: String
    )
}
