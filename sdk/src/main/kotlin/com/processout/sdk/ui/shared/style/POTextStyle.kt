package com.processout.sdk.ui.shared.style

import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize

@Parcelize
data class POTextStyle(
    @ColorInt
    val color: Int,
    val typography: POTypography
) : Parcelable
