package com.processout.sdk.ui.core.style

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class POMessageBoxStyle(
    val text: POTextStyle,
    val border: POBorderStyle,
    @ColorRes
    val backgroundColorResId: Int,
    @DrawableRes
    val iconResId: Int? = null
) : Parcelable
