package com.processout.sdk.ui.core.style

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class PORadioFieldStyle(
    val normal: PORadioFieldStateStyle,
    val selected: PORadioFieldStateStyle,
    val error: PORadioFieldStateStyle,
    val disabled: PORadioFieldStateStyle? = null
) : Parcelable

@Parcelize
data class PORadioFieldStateStyle(
    val title: POTextStyle,
    val option: POTextStyle,
    @ColorRes
    val radioButtonColorResId: Int,
    @ColorRes
    val rowBackgroundColorResId: Int,
    @ColorRes
    val rowRippleColorResId: Int?,
    val border: POBorderStyle
) : Parcelable
