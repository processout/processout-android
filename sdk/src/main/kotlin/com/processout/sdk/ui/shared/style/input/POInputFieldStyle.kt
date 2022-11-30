package com.processout.sdk.ui.shared.style.input

import android.os.Parcelable
import androidx.annotation.ColorInt
import com.processout.sdk.ui.shared.style.POBorderStyle
import com.processout.sdk.ui.shared.style.POTextStyle
import kotlinx.parcelize.Parcelize

@Parcelize
data class POInputFieldStyle(
    val text: POTextStyle,
    val hint: POTextStyle,
    @ColorInt
    val backgroundColor: Int,
    val border: POBorderStyle
) : Parcelable
