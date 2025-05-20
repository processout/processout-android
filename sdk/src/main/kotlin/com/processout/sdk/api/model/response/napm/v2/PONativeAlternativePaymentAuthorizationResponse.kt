package com.processout.sdk.api.model.response.napm.v2

import com.processout.sdk.api.model.response.POBarcode
import com.processout.sdk.api.model.response.POImageResource
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse.CustomerInstruction
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse.NextStep.SubmitData.Parameter
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse.State
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.processout.sdk.core.util.findBy
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** @suppress */
@ProcessOutInternalApi
data class PONativeAlternativePaymentAuthorizationResponse(
    val state: State,
    val nextStep: NextStep?,
    val customerInstructions: List<CustomerInstruction>?
) {

    @JsonClass(generateAdapter = false)
    enum class State {
        NEXT_STEP_REQUIRED,
        PENDING_CAPTURE,
        CAPTURED
    }

    sealed class NextStep {

        data class SubmitData(
            val parameterDefinitions: List<Parameter>
        ) : NextStep() {

            sealed class Parameter {

                @JsonClass(generateAdapter = true)
                data class Text(
                    val key: String,
                    val label: String,
                    val required: Boolean,
                    @Json(name = "min_length")
                    val minLength: Int?,
                    @Json(name = "max_length")
                    val maxLength: Int?
                ) : Parameter()

                @JsonClass(generateAdapter = true)
                data class SingleSelect(
                    val key: String,
                    val label: String,
                    val required: Boolean,
                    @Json(name = "available_values")
                    val availableValues: List<AvailableValue>
                ) : Parameter() {

                    val preselectedValue: AvailableValue?
                        get() = availableValues.find { it.preselected }

                    @JsonClass(generateAdapter = true)
                    data class AvailableValue(
                        val key: String,
                        val label: String,
                        val preselected: Boolean
                    )
                }

                @JsonClass(generateAdapter = true)
                data class Bool(
                    val key: String,
                    val label: String,
                    val required: Boolean
                ) : Parameter()

                @JsonClass(generateAdapter = true)
                data class Digits(
                    val key: String,
                    val label: String,
                    val required: Boolean,
                    @Json(name = "min_length")
                    val minLength: Int?,
                    @Json(name = "max_length")
                    val maxLength: Int?
                ) : Parameter()

                @JsonClass(generateAdapter = true)
                data class Phone(
                    val key: String,
                    val label: String,
                    val required: Boolean,
                    @Json(name = "dialing_code")
                    val dialingCodes: List<DialingCode>?
                ) : Parameter() {

                    @JsonClass(generateAdapter = true)
                    data class DialingCode(
                        val id: String,
                        val value: String
                    )
                }

                @JsonClass(generateAdapter = true)
                data class Email(
                    val key: String,
                    val label: String,
                    val required: Boolean
                ) : Parameter()

                @JsonClass(generateAdapter = true)
                data class Card(
                    val key: String,
                    val label: String,
                    val required: Boolean,
                    @Json(name = "min_length")
                    val minLength: Int?,
                    @Json(name = "max_length")
                    val maxLength: Int?
                ) : Parameter()

                @JsonClass(generateAdapter = true)
                data class Otp(
                    val key: String,
                    @Json(name = "subtype")
                    val rawSubtype: String,
                    val label: String,
                    val required: Boolean,
                    @Json(name = "min_length")
                    val minLength: Int?,
                    @Json(name = "max_length")
                    val maxLength: Int?
                ) : Parameter() {

                    val subtype: Subtype
                        get() = Subtype::rawType.findBy(rawSubtype) ?: Subtype.UNKNOWN

                    enum class Subtype(val rawType: String) {
                        TEXT("text"),
                        DIGITS("digits"),
                        UNKNOWN(String())
                    }
                }

                data object Unknown : Parameter()
            }
        }

        data class Redirect(
            val url: String
        ) : NextStep()

        data object Unknown : NextStep()
    }

    sealed class CustomerInstruction {

        @JsonClass(generateAdapter = true)
        data class Text(
            val label: String?,
            val value: String
        ) : CustomerInstruction()

        @JsonClass(generateAdapter = true)
        data class Image(
            val value: POImageResource
        ) : CustomerInstruction()

        @JsonClass(generateAdapter = true)
        data class Barcode(
            @Json(name = "subtype")
            val rawSubtype: String,
            @Json(name = "value")
            val rawValue: String
        ) : CustomerInstruction() {

            val value: POBarcode
                get() = POBarcode(
                    rawType = rawSubtype,
                    rawValue = rawValue
                )
        }

        @JsonClass(generateAdapter = true)
        data class Group(
            val label: String?,
            val instructions: List<CustomerInstruction>
        ) : CustomerInstruction()

        data object Unknown : CustomerInstruction()
    }
}

@JsonClass(generateAdapter = true)
internal data class NativeAlternativePaymentAuthorizationResponseBody(
    val state: State,
    @Json(name = "next_step")
    val nextStep: NextStep?,
    @Json(name = "customer_instructions")
    val customerInstructions: List<CustomerInstruction>?
) {

    sealed class NextStep {

        @JsonClass(generateAdapter = true)
        data class SubmitData(
            val parameters: Parameters
        ) : NextStep() {

            @JsonClass(generateAdapter = true)
            data class Parameters(
                @Json(name = "parameter_definitions")
                val parameterDefinitions: List<Parameter>
            )
        }

        @JsonClass(generateAdapter = true)
        data class Redirect(
            val parameters: Parameters
        ) : NextStep() {

            @JsonClass(generateAdapter = true)
            data class Parameters(
                val url: String
            )
        }

        data object Unknown : NextStep()
    }
}
