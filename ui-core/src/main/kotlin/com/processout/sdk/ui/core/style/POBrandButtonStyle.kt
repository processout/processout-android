package com.processout.sdk.ui.core.style

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class POBrandButtonStyle(
    val normal: POBrandButtonStateStyle,
    val highlighted: POBrandButtonHighlightedStyle? = null
) : Parcelable

@Parcelize
data class POBrandButtonStateStyle(
    val text: POTextStyle,
    val border: POBorderStyle,
    val backgroundColor: POColor? = null,
    val elevationDp: Int,
    val paddingHorizontalDp: Int = POButtonDefaults.PADDING_HORIZONTAL_DP,
    val paddingVerticalDp: Int = POButtonDefaults.PADDING_VERTICAL_DP
) : Parcelable {

    @Parcelize
    data class POTextStyle(
        val color: POColor,
        val type: POTextType
    ) : Parcelable

    @Parcelize
    data class POBorderStyle(
        val radiusDp: Int,
        val widthDp: Int,
        val color: POColor
    ) : Parcelable
}

@Parcelize
data class POBrandButtonHighlightedStyle(
    val textColor: POColor,
    val borderColor: POColor,
    val backgroundColor: POColor
) : Parcelable
