package com.processout.sdk.ui.core.shared.image

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Specifies rendering modes for an image.
 * Rendering mode controls how color information will be used to display an image.
 */
@Parcelize
enum class POImageRenderingMode : Parcelable {
    /**
     * Renders the original image as-is.
     */
    ORIGINAL,

    /**
     * Renders the image as a template, ignoring its color information.
     */
    TEMPLATE
}
