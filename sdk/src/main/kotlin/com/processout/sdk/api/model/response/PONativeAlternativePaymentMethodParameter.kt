package com.processout.sdk.api.model.response

import com.processout.sdk.core.util.findBy
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
    val displayName: String,
    @Json(name = "available_values")
    val availableValues: List<ParameterValue>?
) {

    fun type() = ParameterType::rawType.findBy(rawType) ?: ParameterType.UNKNOWN

    @JsonClass(generateAdapter = false)
    enum class ParameterType(val rawType: String) {
        NUMERIC("numeric"),
        TEXT("text"),
        EMAIL("email"),
        PHONE("phone"),
        SINGLE_SELECT("single-select"),
        UNKNOWN(String())
    }

    @JsonClass(generateAdapter = true)
    data class ParameterValue(
        val value: String,
        @Json(name = "display_name")
        val displayName: String,
        val default: Boolean?
    )
}
