package com.processout.sdk.ui.core.style

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class PORadioButtonStyle(
    val normal: PORadioButtonStateStyle,
    val selected: PORadioButtonStateStyle,
    val error: PORadioButtonStateStyle
) : Parcelable

@Parcelize
data class PORadioButtonStateStyle(
    @ColorRes
    val buttonColorResId: Int,
    val text: POTextStyle
) : Parcelable
