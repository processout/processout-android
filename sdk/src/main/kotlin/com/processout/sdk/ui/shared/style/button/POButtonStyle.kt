package com.processout.sdk.ui.shared.style.button

import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize

@Parcelize
data class POButtonStyle(
    val normal: POButtonStateStyle,
    val disabled: POButtonStateStyle,
    @ColorInt
    val highlightedBackgroundColor: Int,
    @ColorInt
    val progressIndicatorColor: Int
) : Parcelable
