@file:Suppress("unused")

package com.processout.sdk.ui.napm.delegate.v2

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.processout.sdk.core.logger.POLogger

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

        /**
         * Phone number value.
         *
         * @param[internationalNumber] International phone number.
         */
        fun phoneNumber(internationalNumber: String): PONativeAlternativePaymentParameterValue =
            try {
                val phoneNumberUtil = PhoneNumberUtil.getInstance()
                val parsedNumber = phoneNumberUtil.parse(internationalNumber, null)
                PONativeAlternativePaymentParameterValue(
                    Value.PhoneNumber(
                        regionCode = phoneNumberUtil.getRegionCodeForNumber(parsedNumber) ?: String(),
                        number = parsedNumber.nationalNumber.toString()
                    )
                )
            } catch (e: NumberParseException) {
                POLogger.debug("Failed to parse international phone number [%s]: %s", internationalNumber, e)
                PONativeAlternativePaymentParameterValue(
                    Value.PhoneNumber(
                        regionCode = String(),
                        number = String()
                    )
                )
            }
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
