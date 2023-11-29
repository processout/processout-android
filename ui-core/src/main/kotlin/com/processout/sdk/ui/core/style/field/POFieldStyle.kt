package com.processout.sdk.ui.core.style.field

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class POFieldStyle(
    val normal: POFieldStateStyle,
    val error: POFieldStateStyle
) : Parcelable
