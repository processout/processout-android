package com.processout.sdk.api.model.response

import com.squareup.moshi.JsonClass

/**
 * Color for light/dark themes.
 *
 * @param[light] Light color HEX.
 * @param[dark] Dark color HEX.
 */
@JsonClass(generateAdapter = true)
data class POColor(
    val light: String,
    val dark: String
)
