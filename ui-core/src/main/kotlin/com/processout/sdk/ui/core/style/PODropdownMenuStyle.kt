package com.processout.sdk.ui.core.style

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class PODropdownMenuStyle(
    val text: POTextStyle,
    @ColorRes
    val backgroundColorResId: Int,
    @ColorRes
    val rippleColorResId: Int,
    val border: POBorderStyle
) : Parcelable
