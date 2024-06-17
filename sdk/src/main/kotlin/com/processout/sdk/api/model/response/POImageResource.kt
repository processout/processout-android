package com.processout.sdk.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Image resource for light/dark themes.
 *
 * @param[lightUrl] URL to image for light theme.
 * @param[darkUrl] URL to image for dark theme.
 */
@JsonClass(generateAdapter = true)
data class POImageResource(
    @Json(name = "light_url")
    val lightUrl: ResourceUrl,
    @Json(name = "dark_url")
    val darkUrl: ResourceUrl?
) {

    /**
     * Image resource URL.
     *
     * @param[raster] URL to raster image.
     */
    @JsonClass(generateAdapter = true)
    data class ResourceUrl(
        val raster: String
    )
}
