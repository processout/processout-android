package com.processout.sdk.ui.shared.style.background

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class POBackgroundDecorationStyle(
    val normal: POBackgroundDecorationStateStyle,
    val success: POBackgroundDecorationStateStyle
) : Parcelable
