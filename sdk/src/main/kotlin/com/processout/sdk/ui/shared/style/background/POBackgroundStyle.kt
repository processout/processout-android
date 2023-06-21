package com.processout.sdk.ui.shared.style.background

import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize

@Parcelize
data class POBackgroundStyle(
    @ColorInt
    val normal: Int,
    @ColorInt
    val success: Int
) : Parcelable
