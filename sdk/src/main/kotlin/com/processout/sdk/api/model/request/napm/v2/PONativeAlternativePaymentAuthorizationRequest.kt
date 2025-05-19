package com.processout.sdk.api.model.request.napm.v2

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request parameters for native alternative payment authorization.
 *
 * @param[invoiceId] Invoice identifier.
 * @param[gatewayConfigurationId] Gateway configuration identifier.
 * @param[parameters] Payment parameters.
 */
/** @suppress */
@ProcessOutInternalApi
data class PONativeAlternativePaymentAuthorizationRequest(
    val invoiceId: String,
    val gatewayConfigurationId: String,
    val parameters: Map<String, Parameter>? = null
) {

    /** Payment parameter. */
    sealed class Parameter {
        /** Arbitrary value. */
        data class Value(val value: String) : Parameter()

        /** Phone number value. */
        data class Phone(
            val dialingCode: String,
            val value: String
        ) : Parameter()
    }
}

@JsonClass(generateAdapter = true)
internal data class NativeAlternativePaymentAuthorizationRequestBody(
    @Json(name = "gateway_configuration_id")
    val gatewayConfigurationId: String,
    @Json(name = "submit_data")
    val submitData: SubmitData?
) {

    @JsonClass(generateAdapter = true)
    data class SubmitData(
        val parameters: Map<String, Parameter>
    )

    @JsonClass(generateAdapter = true)
    data class Parameter(
        val value: String,
        @Json(name = "dialing_code")
        val dialingCode: String?
    )
}
