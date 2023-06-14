package com.processout.sdk.ui.shared.style.radio

import android.os.Parcelable
import androidx.annotation.ColorInt
import com.processout.sdk.ui.shared.style.POTextStyle
import kotlinx.parcelize.Parcelize

@Parcelize
data class PORadioButtonStateStyle(
    @ColorInt
    val knobColor: Int,
    val text: POTextStyle
) : Parcelable
