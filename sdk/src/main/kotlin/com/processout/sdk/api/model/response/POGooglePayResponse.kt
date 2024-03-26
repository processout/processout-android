package com.processout.sdk.api.model.response

data class POGooglePayResponse(
    val card: POCard,
    val paymentData: POGooglePayPaymentData
)
