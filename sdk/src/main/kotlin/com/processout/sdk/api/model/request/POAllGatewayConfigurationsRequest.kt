package com.processout.sdk.api.model.request

import com.squareup.moshi.JsonClass

data class POAllGatewayConfigurationsRequest(
    val filter: Filter? = null,
    val withDisabled: Boolean = false
) {
    @JsonClass(generateAdapter = false)
    enum class Filter(val queryValue: String) {
        CARD_PAYMENTS("card-payments"),
        ALTERNATIVE_PAYMENT_METHODS("alternative-payment-methods"),
        ALTERNATIVE_PAYMENT_METHODS_WITH_TOKENIZATION("alternative-payment-methods-with-tokenization"),
        NATIVE_ALTERNATIVE_PAYMENT_METHODS("native-alternative-payment-methods")
    }
}
