package com.processout.sdk.ui.core.style.radio

import android.os.Parcelable
import androidx.annotation.ColorRes
import com.processout.sdk.ui.core.style.text.POTextStyle
import kotlinx.parcelize.Parcelize

@Parcelize
data class PORadioButtonStateStyle(
    @ColorRes
    val knobColorResId: Int,
    val text: POTextStyle
) : Parcelable
