package com.processout.sdk.ui.shared.extension

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.processout.sdk.api.model.response.POColor

internal fun Color.isLight(
    threshold: Float = 0.5f
): Boolean = luminance() > threshold

internal fun Color.lighter(factor: Float) =
    Color(ColorUtils.blendARGB(this.toArgb(), Color.White.toArgb(), factor))

internal fun Color.darker(factor: Float) =
    Color(ColorUtils.blendARGB(this.toArgb(), Color.Black.toArgb(), factor))

internal val POColor.lightColor: Color?
    get() = color(rgba = light)

internal val POColor.darkColor: Color?
    get() = color(rgba = dark)

/**
 * Expected [rgba] format: 0xFFFFFFFF
 */
private fun color(rgba: String): Color? {
    try {
        val hex = rgba.substring(startIndex = 2).toLong(radix = 16)
        return Color(
            red = (hex shr 24 and 0xFF).toInt(),
            green = (hex shr 16 and 0xFF).toInt(),
            blue = (hex shr 8 and 0xFF).toInt(),
            alpha = (hex shr 0 and 0xFF).toInt()
        )
    } catch (_: Exception) {
        return null
    }
}
