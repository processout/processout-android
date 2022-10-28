package com.processout.sdk.api.model.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentMethodParameter(
    val key: String,
    val length: Int?,
    val required: Boolean,
    val type: ParameterType
) {
    enum class ParameterType {
        numeric,
        text,
        email,
        phone
    }
}
