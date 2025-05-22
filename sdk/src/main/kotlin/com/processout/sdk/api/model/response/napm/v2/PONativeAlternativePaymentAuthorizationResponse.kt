package com.processout.sdk.api.model.response.napm.v2

import com.processout.sdk.api.model.response.POBarcode
import com.processout.sdk.api.model.response.POImageResource
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse.CustomerInstruction
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse.State
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentNextStep.SubmitData.Parameter
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Specifies details of native alternative payment.
 *
 * @param[state] State of native alternative payment.
 * @param[nextStep] Next required step in the payment flow.
 * @param[customerInstructions] Instructions for the customer that provide additional information and/or describe required actions.
 */
/** @suppress */
@ProcessOutInternalApi
data class PONativeAlternativePaymentAuthorizationResponse(
    val state: State,
    val nextStep: PONativeAlternativePaymentNextStep?,
    val customerInstructions: List<CustomerInstruction>?
) {

    /**
     * State of native alternative payment.
     */
    @JsonClass(generateAdapter = false)
    enum class State {
        /** Next step is required to proceed. */
        NEXT_STEP_REQUIRED,

        /** Payment is ready to be captured. */
        PENDING_CAPTURE,

        /** Payment is captured. */
        CAPTURED,

        /**
         * Placeholder that allows adding additional cases while staying backward compatible.
         * __Warning:__ Do not match this case directly, use _when-else_ instead.
         */
        @ProcessOutInternalApi
        UNKNOWN
    }

    /**
     * Specifies instruction for the customer, providing additional information and/or describing required actions.
     */
    sealed class CustomerInstruction {

        /**
         * Customer instruction provided as a markdown text.
         *
         * @param[label] Optional instruction display label.
         * @param[value] Markdown text.
         */
        @JsonClass(generateAdapter = true)
        data class Text(
            val label: String?,
            val value: String
        ) : CustomerInstruction()

        /**
         * Customer instruction provided as an image resource.
         *
         * @param[value] Image resource.
         */
        @JsonClass(generateAdapter = true)
        data class Image(
            val value: POImageResource
        ) : CustomerInstruction()

        /**
         * Customer instruction provided via barcode.
         *
         * @param[rawSubtype] Raw barcode subtype.
         * @param[rawValue] Base64 encoded value.
         */
        @JsonClass(generateAdapter = true)
        data class Barcode(
            @Json(name = "subtype")
            val rawSubtype: String,
            @Json(name = "value")
            val rawValue: String
        ) : CustomerInstruction() {

            /** Barcode value. */
            val value: POBarcode
                get() = POBarcode(
                    rawType = rawSubtype,
                    rawValue = rawValue
                )
        }

        /**
         * Group of customer instructions.
         *
         * @param[label] Optional group display label.
         * @param[instructions] Grouped instructions for the customer that provide additional information and/or describe required actions.
         */
        @JsonClass(generateAdapter = true)
        data class Group(
            val label: String?,
            val instructions: List<CustomerInstruction>
        ) : CustomerInstruction()

        /**
         * Placeholder that allows adding additional cases while staying backward compatible.
         * __Warning:__ Do not match this case directly, use _when-else_ instead.
         */
        @ProcessOutInternalApi
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
