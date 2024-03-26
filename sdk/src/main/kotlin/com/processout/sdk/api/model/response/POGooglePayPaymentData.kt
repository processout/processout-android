package com.processout.sdk.api.model.response

import com.squareup.moshi.JsonClass

/**
 * Corresponds to Google Pay
 * [PaymentData](https://developers.google.com/pay/api/android/reference/response-objects#PaymentData).
 */
@JsonClass(generateAdapter = true)
data class POGooglePayPaymentData(
    val apiVersion: Int,
    val apiVersionMinor: Int,
    val paymentMethodData: PaymentMethodData,
    val email: String?,
    val shippingAddress: Address?
) {

    /**
     * Corresponds to Google Pay
     * [PaymentMethodData](https://developers.google.com/pay/api/android/reference/response-objects#PaymentMethodData).
     */
    @JsonClass(generateAdapter = true)
    data class PaymentMethodData(
        val type: String,
        val description: String,
        val info: CardInfo,
        val tokenizationData: PaymentMethodTokenizationData
    )

    /**
     * Corresponds to Google Pay
     * [CardInfo](https://developers.google.com/pay/api/android/reference/response-objects#CardInfo).
     */
    @JsonClass(generateAdapter = true)
    data class CardInfo(
        val cardDetails: String,
        val assuranceDetails: AssuranceDetailsSpecifications,
        val cardNetwork: String,
        val billingAddress: Address?
    )

    /**
     * Corresponds to Google Pay
     * [AssuranceDetailsSpecifications](https://developers.google.com/pay/api/android/reference/response-objects#assurance-details-specifications).
     */
    @JsonClass(generateAdapter = true)
    data class AssuranceDetailsSpecifications(
        val accountVerified: Boolean,
        val cardHolderAuthenticated: Boolean
    )

    /**
     * Corresponds to Google Pay
     * [Address](https://developers.google.com/pay/api/android/reference/response-objects#Address).
     */
    @JsonClass(generateAdapter = true)
    data class Address(
        val name: String,
        val postalCode: String,
        val countryCode: String,
        val phoneNumber: String?,
        val address1: String,
        val address2: String,
        val address3: String,
        val locality: String,
        val administrativeArea: String,
        val sortingCode: String
    )

    /**
     * Corresponds to Google Pay
     * [PaymentMethodTokenizationData](https://developers.google.com/pay/api/android/reference/response-objects#PaymentMethodTokenizationData).
     */
    @JsonClass(generateAdapter = true)
    data class PaymentMethodTokenizationData(
        val type: String,
        val token: String?
    )
}
