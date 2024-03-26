package com.processout.sdk.api.model.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class POGooglePayPaymentData(
    val apiVersion: Int,
    val apiVersionMinor: Int,
    val paymentMethodData: PaymentMethodData,
    val email: String?,
    val shippingAddress: Address?
) {

    @JsonClass(generateAdapter = true)
    data class PaymentMethodData(
        val type: String,
        val description: String,
        val info: CardInfo,
        val tokenizationData: PaymentMethodTokenizationData
    )

    @JsonClass(generateAdapter = true)
    data class CardInfo(
        val cardDetails: String,
        val assuranceDetails: AssuranceDetailsSpecifications,
        val cardNetwork: String,
        val billingAddress: Address?
    )

    @JsonClass(generateAdapter = true)
    data class AssuranceDetailsSpecifications(
        val accountVerified: Boolean,
        val cardHolderAuthenticated: Boolean
    )

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

    @JsonClass(generateAdapter = true)
    data class PaymentMethodTokenizationData(
        val type: String,
        val token: String?
    )
}
