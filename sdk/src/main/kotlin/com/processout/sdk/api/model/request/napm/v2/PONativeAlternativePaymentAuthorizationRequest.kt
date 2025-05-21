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

    /**
     * Payment parameter.
     */
    sealed class Parameter {
        /**
         * Arbitrary string value.
         */
        data class String(val value: kotlin.String) : Parameter()

        /**
         * Phone number value.
         *
         * @param[dialingCode] International dialing code.
         * @param[number] The rest of the number without dialing code.
         */
        data class PhoneNumber(
            val dialingCode: kotlin.String,
            val number: kotlin.String
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
