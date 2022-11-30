package com.processout.sdk.ui.shared.style.input

import android.os.Parcelable
import com.processout.sdk.ui.shared.style.POTextStyle
import kotlinx.parcelize.Parcelize

@Parcelize
data class POInputStateStyle(
    val title: POTextStyle,
    val field: POInputFieldStyle,
    val description: POTextStyle
) : Parcelable
