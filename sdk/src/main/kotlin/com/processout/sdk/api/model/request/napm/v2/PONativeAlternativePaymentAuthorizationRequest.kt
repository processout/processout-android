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
    data class Parameter internal constructor(
        @ProcessOutInternalApi val value: Value
    ) {

        companion object {
            /**
             * Arbitrary string parameter.
             */
            fun string(value: String) = Parameter(Value.String(value))

            /**
             * Phone number parameter.
             *
             * @param[dialingCode] International dialing code.
             * @param[number] The rest of the number without dialing code.
             */
            fun phoneNumber(
                dialingCode: String,
                number: String
            ) = Parameter(
                Value.PhoneNumber(
                    dialingCode = dialingCode,
                    number = number
                )
            )
        }

        /** @suppress */
        @ProcessOutInternalApi
        sealed class Value {
            data class String(
                val value: kotlin.String
            ) : Value()

            @JsonClass(generateAdapter = true)
            data class PhoneNumber(
                @Json(name = "dialing_code")
                val dialingCode: kotlin.String,
                val number: kotlin.String
            ) : Value()
        }
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
        val parameters: Map<String, Any>
    )
}
