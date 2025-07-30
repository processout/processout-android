package com.processout.sdk.ui.core.style

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class POLabeledContentStyle(
    val label: POTextStyle,
    val text: POTextStyle
) : Parcelable
