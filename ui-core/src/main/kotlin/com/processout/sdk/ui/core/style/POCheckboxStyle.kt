package com.processout.sdk.ui.core.style

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class POCheckboxStyle(
    val normal: POCheckboxStateStyle,
    val selected: POCheckboxStateStyle,
    val error: POCheckboxStateStyle,
    val disabled: POCheckboxStateStyle
) : Parcelable

@Parcelize
data class POCheckboxStateStyle(
    val checkmark: POCheckmarkStyle,
    val text: POTextStyle
) : Parcelable

@Parcelize
data class POCheckmarkStyle(
    @ColorRes
    val colorResId: Int,
    @ColorRes
    val borderColorResId: Int,
    @ColorRes
    val backgroundColorResId: Int,
) : Parcelable
