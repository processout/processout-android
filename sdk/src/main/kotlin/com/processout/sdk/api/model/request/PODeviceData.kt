package com.processout.sdk.api.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class PODeviceData(
    @Json(name = "app_language")
    val appLanguage: String,
    @Json(name = "app_screen_width")
    val appScreenWidth: Int,
    @Json(name = "app_screen_height")
    val appScreenHeight: Int,
    @Json(name = "app_timezone_offset")
    val appTimeZoneOffSet: Int,
)
