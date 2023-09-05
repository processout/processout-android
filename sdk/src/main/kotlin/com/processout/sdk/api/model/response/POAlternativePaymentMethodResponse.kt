package com.processout.sdk.api.model.response

import com.squareup.moshi.JsonClass

/**
 * Response of the alternative payment.
 *
 * @param gatewayToken Gateway token starting with the prefix _gway_req__ that can be used to perform a sale call.
 * @param customerId Customer ID that may be used for creating APM recurring token.
 * @param tokenId Customer token ID that may be used for creating APM recurring token.
 * @param returnType Indicates if this is an APM token creation or a payment creation response.
 */
data class POAlternativePaymentMethodResponse(
    val gatewayToken: String,
    val customerId: String?,
    val tokenId: String?,
    val returnType: APMReturnType
) {
    @JsonClass(generateAdapter = false)
    enum class APMReturnType {
        AUTHORIZATION,
        CREATE_TOKEN
    }
}
