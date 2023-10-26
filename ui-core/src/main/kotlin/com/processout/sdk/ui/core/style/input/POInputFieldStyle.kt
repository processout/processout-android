package com.processout.sdk.ui.core.style.input

import android.os.Parcelable
import androidx.annotation.ColorRes
import com.processout.sdk.ui.core.style.POBorderStyle
import com.processout.sdk.ui.core.style.POTextStyle
import kotlinx.parcelize.Parcelize

@Parcelize
data class POInputFieldStyle(
    val text: POTextStyle,
    @ColorRes
    val hintTextColorResId: Int,
    @ColorRes
    val backgroundColorResId: Int,
    @ColorRes
    val controlsTintColorResId: Int,
    val border: POBorderStyle
) : Parcelable
