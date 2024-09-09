package com.processout.sdk.ui.core.style

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

@Parcelize
data class POButtonStateStyle(
    val text: POTextStyle,
    val border: POBorderStyle,
    @ColorRes
    val backgroundColorResId: Int,
    val elevationDp: Int,
    val paddingHorizontalDp: Int = POButtonDefaults.PADDING_HORIZONTAL_DP,
    val paddingVerticalDp: Int = POButtonDefaults.PADDING_VERTICAL_DP
) : Parcelable

@Parcelize
data class POButtonHighlightedStyle(
    @ColorRes
    val textColorResId: Int,
    @ColorRes
    val borderColorResId: Int,
    @ColorRes
    val backgroundColorResId: Int
) : Parcelable
