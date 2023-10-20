package com.processout.sdk.ui.core.style.button

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class POButtonStyle(
    val normal: POButtonStateStyle,
    val disabled: POButtonStateStyle,
    val highlighted: POButtonHighlightedStyle,
    @ColorRes
    val progressIndicatorColorResId: Int
) : Parcelable
