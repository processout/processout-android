package com.processout.sdk.ui.shared.style.button

import android.os.Parcelable
import androidx.annotation.ColorInt
import com.processout.sdk.ui.shared.style.POBorderStyle
import com.processout.sdk.ui.shared.style.POTextStyle
import kotlinx.parcelize.Parcelize

@Parcelize
data class POButtonStateStyle(
    val title: POTextStyle,
    val border: POBorderStyle,
    @ColorInt
    val backgroundColor: Int
) : Parcelable
