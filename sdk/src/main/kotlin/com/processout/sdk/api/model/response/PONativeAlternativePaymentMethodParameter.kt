package com.processout.sdk.api.model.response

import com.processout.sdk.utils.findBy
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentMethodParameter(
    val key: String,
    val length: Int?,
    val required: Boolean,
    @Json(name = "type")
    val rawType: String,
    @Json(name = "display_name")
    val displayName: String
) {

    fun type() = ParameterType::rawType.findBy(rawType) ?: ParameterType.UNKNOWN

    enum class ParameterType(val rawType: String) {
        NUMERIC("numeric"),
        TEXT("text"),
        EMAIL("email"),
        PHONE("phone"),
        SINGLE_SELECT("single-select"),
        UNKNOWN(String())
    }
}
