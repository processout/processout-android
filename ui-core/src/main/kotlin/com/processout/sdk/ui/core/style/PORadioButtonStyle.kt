package com.processout.sdk.ui.core.style

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize

@Deprecated(message = "Only used in deprecated implementation.")
@Parcelize
data class PORadioButtonStyle(
    val normal: PORadioButtonStateStyle,
    val selected: PORadioButtonStateStyle,
    val error: PORadioButtonStateStyle,
    val disabled: PORadioButtonStateStyle? = null
) : Parcelable

@Deprecated(message = "Only used in deprecated implementation.")
@Parcelize
data class PORadioButtonStateStyle(
    @ColorRes
    val buttonColorResId: Int,
    val text: POTextStyle
) : Parcelable
