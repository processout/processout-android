package com.processout.sdk.api.model.response

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * Image resource for light/dark themes.
 *
 * @param[lightUrl] URL to image for light theme.
 * @param[darkUrl] URL to image for dark theme.
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class POImageResource(
    @Json(name = "light_url")
    val lightUrl: ResourceUrl,
    @Json(name = "dark_url")
    val darkUrl: ResourceUrl?
) : Parcelable {

    /**
     * Image resource URL.
     *
     * @param[raster] URL to raster image.
     */
    @Parcelize
    @JsonClass(generateAdapter = true)
    data class ResourceUrl(
        val raster: String
    ) : Parcelable
}
