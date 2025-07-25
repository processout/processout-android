package com.processout.sdk.api.model.response.napm.v2

import com.processout.sdk.api.model.response.POBarcode
import com.processout.sdk.api.model.response.POImageResource
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Specifies instruction for the customer, providing additional information and/or describing required actions.
 */
sealed class PONativeAlternativePaymentCustomerInstruction {

    /**
     * Customer instruction provided as a markdown text.
     *
     * @param[label] Optional label.
     * @param[value] Markdown text.
     */
    @JsonClass(generateAdapter = true)
    data class Message(
        val label: String?,
        val value: String
    ) : PONativeAlternativePaymentCustomerInstruction()

    /**
     * Customer instruction provided as an image resource.
     *
     * @param[value] Image resource.
     */
    @JsonClass(generateAdapter = true)
    data class Image(
        val value: POImageResource
    ) : PONativeAlternativePaymentCustomerInstruction()

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
    ) : PONativeAlternativePaymentCustomerInstruction() {

        /** Barcode value. */
        val value: POBarcode
            get() = POBarcode(
                rawType = rawSubtype,
                rawValue = rawValue
            )
    }

    /**
     * Placeholder that allows adding additional cases while staying backward compatible.
     * __Warning:__ Do not match this case directly, use _when-else_ instead.
     */
    @ProcessOutInternalApi
    data object Unknown : PONativeAlternativePaymentCustomerInstruction()
}
