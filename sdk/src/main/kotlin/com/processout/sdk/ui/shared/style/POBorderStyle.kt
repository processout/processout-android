package com.processout.sdk.ui.shared.style

import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize

@Parcelize
data class POBorderStyle(
    val radiusDp: Int,
    val widthDp: Int,
    @ColorInt
    val color: Int
) : Parcelable
