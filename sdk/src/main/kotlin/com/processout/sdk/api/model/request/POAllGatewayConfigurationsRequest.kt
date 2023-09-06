package com.processout.sdk.api.model.request

import com.squareup.moshi.JsonClass

/**
 * Request parameters used to fetch gateway configurations.
 *
 * @param[filter] Applies gateway configurations filter if specified, otherwise will return all available.
 * @param[withDisabled] Flag indicating whether disabled gateway configurations should be included in response.
 */
data class POAllGatewayConfigurationsRequest(
    val filter: Filter? = null,
    val withDisabled: Boolean = false
) {
    /**
     * Defines filters for gateway configurations.
     */
    @JsonClass(generateAdapter = false)
    enum class Filter(val queryValue: String) {
        /** Gateways that support card payments only. */
        CARD_PAYMENTS("card-payments"),

        /** Gateways that support alternative payment methods. */
        ALTERNATIVE_PAYMENT_METHODS("alternative-payment-methods"),

        /** Gateways that support alternative payment methods with tokenization. */
        ALTERNATIVE_PAYMENT_METHODS_WITH_TOKENIZATION("alternative-payment-methods-with-tokenization"),

        /** Gateways that support native alternative payment methods. */
        NATIVE_ALTERNATIVE_PAYMENT_METHODS("native-alternative-payment-methods")
    }
}
