package com.processout.sdk.api.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class DeviceData(
    @Json(name = "app_language")
    val appLanguage: String,
    @Json(name = "app_screen_width")
    val screenWidth: Int,
    @Json(name = "app_screen_height")
    val screenHeight: Int,
    @Json(name = "app_timezone_offset")
    val timeZoneOffset: Int,
    val channel: String = "android",
    @Json(ignore = true)
    val model: String = ""
)
