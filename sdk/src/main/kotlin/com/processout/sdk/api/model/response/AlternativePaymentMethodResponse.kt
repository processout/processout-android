package com.processout.sdk.api.model.response

data class POAlternativePaymentMethodResponse (
    // Gateway token starting with prefix gway_req_ that can be used to perform a sale call.
    val gatewayToken: String?,
    
    // Customer  ID that may be used for creating APM recurring token.
    val customerId: String?,
    
    // Customer token ID that may be used for creating APM recurring token.
    val tokenId: String?,
    
    // returnType informs if this was an APM token creation or a payment creation response.
    val returnType: APMReturnType?,
) {
    enum class APMReturnType(val value: String) {
        AUTHORIZATION("AUTHORIZATION"),
        CREATE_TOKEN("CREATETOKEN")
    }
}
