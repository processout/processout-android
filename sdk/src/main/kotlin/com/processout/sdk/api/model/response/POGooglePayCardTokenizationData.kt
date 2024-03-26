package com.processout.sdk.api.model.response

/**
 * Defines the Google Pay card tokenization data.
 *
 * @param[card] Details of tokenized card returned by ProcessOut.
 * @param[paymentData] Mapped [PaymentData](https://developers.google.com/pay/api/android/reference/response-objects#PaymentData)
 * returned by Google Pay.
 */
data class POGooglePayCardTokenizationData(
    val card: POCard,
    val paymentData: POGooglePayPaymentData
)
