package com.processout.sdk.ui.core.style.input

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class POInputStyle(
    val normal: POInputStateStyle,
    val error: POInputStateStyle
) : Parcelable
