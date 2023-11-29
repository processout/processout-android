package com.processout.sdk.ui.core.style.radio

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.processout.sdk.ui.core.style.POTextStyle
import kotlinx.parcelize.Parcelize

@Parcelize
data class PORadioButtonStyle(
    val title: POTextStyle,
    val normal: PORadioButtonStateStyle,
    val selected: PORadioButtonStateStyle,
    val error: PORadioButtonStateStyle,
    val errorDescription: POTextStyle,
    @DrawableRes
    val knobDrawableResId: Int? = null
) : Parcelable
