package com.processout.sdk.api.model.response.napm.v2

import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentElement.Form.Parameter
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.processout.sdk.core.util.findBy
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Specifies an element that needs to be rendered on the UI during native alternative payment flow.
 */
/** @suppress */
@ProcessOutInternalApi
sealed class PONativeAlternativePaymentElement {

    /**
     * A form element with the specified [parameterDefinitions].
     */
    data class Form(
        val parameterDefinitions: List<Parameter>
    ) : PONativeAlternativePaymentElement() {

        /**
         * Payment parameter definition.
         */
        sealed class Parameter {

            /** Parameter key. */
            abstract val key: String

            /** Parameter display label. */
            abstract val label: String

            /** Indicates whether the parameter is required. */
            abstract val required: Boolean

            /**
             * Text parameter.
             *
             * @param[key] Parameter key.
             * @param[label] Parameter display label.
             * @param[required] Indicates whether the parameter is required.
             * @param[minLength] Optional minimum length.
             * @param[maxLength] Optional maximum length.
             */
            @JsonClass(generateAdapter = true)
            data class Text(
                override val key: String,
                override val label: String,
                override val required: Boolean,
                @Json(name = "min_length")
                val minLength: Int?,
                @Json(name = "max_length")
                val maxLength: Int?
            ) : Parameter()

            /**
             * Single selection parameter.
             *
             * @param[key] Parameter key.
             * @param[label] Parameter display label.
             * @param[required] Indicates whether the parameter is required.
             * @param[availableValues] Available values.
             */
            @JsonClass(generateAdapter = true)
            data class SingleSelect(
                override val key: String,
                override val label: String,
                override val required: Boolean,
                @Json(name = "available_values")
                val availableValues: List<AvailableValue>
            ) : Parameter() {

                /** Preselected value. */
                val preselectedValue: AvailableValue?
                    get() = availableValues.find { it.preselected }

                /**
                 * Available parameter value.
                 *
                 * @param[value] Parameter value.
                 * @param[label] Value display label.
                 * @param[preselected] Indicates whether the value should be preselected by default.
                 */
                @JsonClass(generateAdapter = true)
                data class AvailableValue(
                    val value: String,
                    val label: String,
                    val preselected: Boolean
                )
            }

            /**
             * Boolean parameter.
             *
             * @param[key] Parameter key.
             * @param[label] Parameter display label.
             * @param[required] Indicates whether the parameter is required.
             */
            @JsonClass(generateAdapter = true)
            data class Bool(
                override val key: String,
                override val label: String,
                override val required: Boolean
            ) : Parameter()

            /**
             * Digits only parameter.
             *
             * @param[key] Parameter key.
             * @param[label] Parameter display label.
             * @param[required] Indicates whether the parameter is required.
             * @param[minLength] Optional minimum length.
             * @param[maxLength] Optional maximum length.
             */
            @JsonClass(generateAdapter = true)
            data class Digits(
                override val key: String,
                override val label: String,
                override val required: Boolean,
                @Json(name = "min_length")
                val minLength: Int?,
                @Json(name = "max_length")
                val maxLength: Int?
            ) : Parameter()

            /**
             * Phone number parameter.
             *
             * @param[key] Parameter key.
             * @param[label] Parameter display label.
             * @param[required] Indicates whether the parameter is required.
             * @param[dialingCodes] Supported international dialing codes.
             */
            @JsonClass(generateAdapter = true)
            data class PhoneNumber(
                override val key: String,
                override val label: String,
                override val required: Boolean,
                @Json(name = "dialing_codes")
                val dialingCodes: List<DialingCode>?
            ) : Parameter() {

                /**
                 * International dialing code.
                 *
                 * @param[id] Country code identifier.
                 * @param[value] Dialing code value.
                 */
                @JsonClass(generateAdapter = true)
                data class DialingCode(
                    val id: String,
                    val value: String
                )
            }

            /**
             * Email parameter.
             *
             * @param[key] Parameter key.
             * @param[label] Parameter display label.
             * @param[required] Indicates whether the parameter is required.
             */
            @JsonClass(generateAdapter = true)
            data class Email(
                override val key: String,
                override val label: String,
                override val required: Boolean
            ) : Parameter()

            /**
             * Card number parameter.
             *
             * @param[key] Parameter key.
             * @param[label] Parameter display label.
             * @param[required] Indicates whether the parameter is required.
             * @param[minLength] Optional minimum length.
             * @param[maxLength] Optional maximum length.
             */
            @JsonClass(generateAdapter = true)
            data class Card(
                override val key: String,
                override val label: String,
                override val required: Boolean,
                @Json(name = "min_length")
                val minLength: Int?,
                @Json(name = "max_length")
                val maxLength: Int?
            ) : Parameter()

            /**
             * One-Time Password (OTP) parameter.
             *
             * @param[key] Parameter key.
             * @param[rawSubtype] Raw OTP subtype.
             * @param[label] Parameter display label.
             * @param[required] Indicates whether the parameter is required.
             * @param[minLength] Optional minimum length.
             * @param[maxLength] Optional maximum length.
             */
            @JsonClass(generateAdapter = true)
            data class Otp(
                override val key: String,
                override val label: String,
                override val required: Boolean,
                @Json(name = "subtype")
                val rawSubtype: String,
                @Json(name = "min_length")
                val minLength: Int?,
                @Json(name = "max_length")
                val maxLength: Int?
            ) : Parameter() {

                /** OTP subtype. */
                val subtype: Subtype
                    get() = Subtype::rawType.findBy(rawSubtype) ?: Subtype.UNKNOWN

                /**
                 * One-Time Password (OTP) subtype.
                 */
                enum class Subtype(val rawType: String) {
                    /** Text OTP. */
                    TEXT("text"),

                    /** Digits only OTP. */
                    DIGITS("digits"),

                    /**
                     * Placeholder that allows adding additional cases while staying backward compatible.
                     * __Warning:__ Do not match this case directly, use _when-else_ instead.
                     */
                    @ProcessOutInternalApi
                    UNKNOWN(String())
                }
            }

            /**
             * Placeholder that allows adding additional cases while staying backward compatible.
             * __Warning:__ Do not match this case directly, use _when-else_ instead.
             */
            @ProcessOutInternalApi
            data object Unknown : Parameter() {
                override val key = String()
                override val label = String()
                override val required = false
            }
        }
    }

    /**
     * Placeholder that allows adding additional cases while staying backward compatible.
     * __Warning:__ Do not match this case directly, use _when-else_ instead.
     */
    @ProcessOutInternalApi
    data object Unknown : PONativeAlternativePaymentElement()
}

internal sealed class NativeAlternativePaymentElement {

    @JsonClass(generateAdapter = true)
    data class Form(
        val parameters: Parameters
    ) : NativeAlternativePaymentElement() {

        @JsonClass(generateAdapter = true)
        data class Parameters(
            @Json(name = "parameter_definitions")
            val parameterDefinitions: List<Parameter>
        )
    }

    data object Unknown : NativeAlternativePaymentElement()
}
