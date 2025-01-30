package com.processout.sdk.ui.core.shared.image

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

/**
 * Specifies drawable image resource.
 *
 * @param[resId] Drawable image resource ID.
 * @param[renderingMode] Specifies image rendering mode.
 */
@Immutable
@Parcelize
data class PODrawableImage(
    @DrawableRes
    val resId: Int,
    val renderingMode: POImageRenderingMode
) : Parcelable
