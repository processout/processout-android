package com.processout.sdk.api.model.response.napm.v2

import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse.NextStep.SubmitData.Parameter
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse.State
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** @suppress */
@ProcessOutInternalApi
data class PONativeAlternativePaymentAuthorizationResponse(
    val state: State,
    val nextStep: NextStep?
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
                    val availableValues: List<AvailableValue>,
                    @Json(ignore = true)
                    val preselectedValue: AvailableValue? = availableValues.find { it.preselected }
                ) : Parameter() {

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
                    val required: Boolean
                ) : Parameter()

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
                    val label: String,
                    val required: Boolean,
                    @Json(name = "min_length")
                    val minLength: Int?,
                    @Json(name = "max_length")
                    val maxLength: Int?
                ) : Parameter()

                data object Unknown : Parameter()
            }
        }

        data class Redirect(
            val url: String
        ) : NextStep()

        data object Unknown : NextStep()
    }
}

@JsonClass(generateAdapter = true)
internal data class NativeAlternativePaymentAuthorizationResponseBody(
    val state: State,
    @Json(name = "next_step")
    val nextStep: NextStep?
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
