package com.processout.sdk.api.model.response

import com.processout.sdk.core.util.findBy
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Native alternative payment method parameter definition.
 *
 * @param[key] Name of the field that needs to be collected for the request (e.g. blik_code).
 * @param[length] Expected length of the field. Used for validation.
 * @param[required] Indicates whether parameter is required or optional.
 * @param[rawType] Raw parameter type.
 * @param[displayName] Parameter’s localized name that could be displayed to user.
 * @param[availableValues] Some types (e.g. single-select) can have a collection of pre-populated values that user can choose from.
 */
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

    /**
     * Returns supported [ParameterType] or [ParameterType.UNKNOWN] otherwise.
     */
    fun type() = ParameterType::rawType.findBy(rawType) ?: ParameterType.UNKNOWN

    /**
     * Defines supported parameter types.
     */
    @JsonClass(generateAdapter = false)
    enum class ParameterType(val rawType: String) {
        /** Numeric only field. */
        NUMERIC("numeric"),

        /** Generic text field. Any chars allowed. */
        TEXT("text"),

        /** Phone field. */
        EMAIL("email"),

        /** Email field. */
        PHONE("phone"),

        /** Phone number field. */
        SINGLE_SELECT("single-select"),

        /** Unsupported field type. */
        UNKNOWN(String())
    }

    /**
     * Defines available parameter value.
     *
     * @param[value] The actual parameter value.
     * @param[displayName] Parameter’s localized name that could be displayed to user.
     * @param[default] Defines whether parameter is pre-selected as a default.
     */
    @JsonClass(generateAdapter = true)
    data class ParameterValue(
        val value: String,
        @Json(name = "display_name")
        val displayName: String,
        val default: Boolean?
    )
}
