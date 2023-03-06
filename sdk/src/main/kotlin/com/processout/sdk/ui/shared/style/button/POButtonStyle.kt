package com.processout.sdk.ui.shared.style.button

import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize

@Parcelize
data class POButtonStyle(
    val normal: POButtonStateStyle,
    val disabled: POButtonStateStyle,
    val highlighted: POButtonHighlightedStyle,
    @ColorInt
    val progressIndicatorColor: Int
) : Parcelable
