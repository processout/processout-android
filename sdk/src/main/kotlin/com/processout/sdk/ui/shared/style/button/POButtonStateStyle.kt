package com.processout.sdk.ui.shared.style.button

import android.os.Parcelable
import androidx.annotation.ColorInt
import com.processout.sdk.ui.shared.style.POBorderStyle
import com.processout.sdk.ui.shared.style.POTextStyle
import kotlinx.parcelize.Parcelize

@Parcelize
data class POButtonStateStyle(
    val text: POTextStyle? = null,
    val border: POBorderStyle? = null,
    @ColorInt
    val backgroundColor: Int
) : Parcelable
