package com.processout.sdk.ui.core.style

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class POFieldStyle(
    val normal: POFieldStateStyle,
    val error: POFieldStateStyle,
    val focused: POFieldStateStyle? = null
) : Parcelable

@Parcelize
data class POFieldStateStyle(
    val text: POTextStyle,
    val label: POTextStyle,
    @ColorRes
    val placeholderTextColorResId: Int,
    @ColorRes
    val backgroundColorResId: Int,
    @ColorRes
    val controlsTintColorResId: Int,
    val border: POBorderStyle,
    @ColorRes
    val dropdownRippleColorResId: Int? = null
) : Parcelable {

    @Deprecated(message = "Use alternative constructor.")
    constructor(
        text: POTextStyle,
        @ColorRes
        placeholderTextColorResId: Int,
        @ColorRes
        backgroundColorResId: Int,
        @ColorRes
        controlsTintColorResId: Int,
        border: POBorderStyle,
        @ColorRes
        dropdownRippleColorResId: Int? = null
    ) : this(
        text = text,
        label = POTextStyle(
            colorResId = 0,
            type = POTextType(
                textSizeSp = 0,
                lineHeightSp = 0
            )
        ),
        placeholderTextColorResId = placeholderTextColorResId,
        backgroundColorResId = backgroundColorResId,
        controlsTintColorResId = controlsTintColorResId,
        border = border,
        dropdownRippleColorResId = dropdownRippleColorResId
    )
}
