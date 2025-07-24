package com.processout.sdk.ui.napm.delegate.v2

/**
 * Native alternative payment parameter value.
 */
data class PONativeAlternativePaymentParameterValue internal constructor(
    internal val value: Value
) {

    companion object {
        /**
         * Arbitrary string value.
         */
        fun string(value: String) = PONativeAlternativePaymentParameterValue(Value.String(value))

        /**
         * Phone number value.
         *
         * @param[regionCode] The region code associated with the phone number.
         * Corresponds to a two-letter ISO 3166-1 alpha-2 country code.
         * @param[number] The rest of the number without dialing code.
         */
        fun phoneNumber(
            regionCode: String,
            number: String
        ) = PONativeAlternativePaymentParameterValue(
            Value.PhoneNumber(
                regionCode = regionCode,
                number = number
            )
        )
    }

    internal sealed class Value {
        data class String(
            val value: kotlin.String
        ) : Value()

        data class PhoneNumber(
            val regionCode: kotlin.String,
            val number: kotlin.String
        ) : Value()
    }
}
