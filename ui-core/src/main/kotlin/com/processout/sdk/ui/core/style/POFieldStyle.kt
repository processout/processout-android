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
    @ColorRes
    val placeholderTextColorResId: Int,
    @ColorRes
    val backgroundColorResId: Int,
    @ColorRes
    val controlsTintColorResId: Int,
    val border: POBorderStyle,
    @ColorRes
    val dropdownRippleColorResId: Int? = null
) : Parcelable
