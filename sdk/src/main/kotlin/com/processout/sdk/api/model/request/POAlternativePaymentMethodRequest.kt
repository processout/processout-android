package com.processout.sdk.api.model.request

data class POAlternativePaymentMethodRequest (
    // Invoice identifier to to perform apm payment for.
    val invoiceId: String? = null,

    // Gateway Configuration ID of the APM the payment will be made on.
    val gatewayConfigurationId: String,

    // Additional Data that will be supplied to the APM.
    val additionalData: Map<String, String>? = null,

    // Customer  ID that may be used for creating APM recurring token.
    val customerId: String? = null,

    // Customer token ID that may be used for creating APM recurring token.
    val tokenId: String? = null,
)
