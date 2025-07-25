package com.processout.sdk.api.model.request.napm.v2

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Specifies native alternative payment payload.
 *
 * @param[parameters] Map of payment parameter values.
 */
data class PONativeAlternativePaymentSubmitData(
    val parameters: Map<String, Parameter>
) {

    /**
     * Payment parameter value.
     */
    data class Parameter internal constructor(
        @ProcessOutInternalApi val value: Value
    ) {

        companion object {
            /**
             * Arbitrary string value.
             */
            fun string(value: String) = Parameter(Value.String(value))

            /**
             * Phone number value.
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
