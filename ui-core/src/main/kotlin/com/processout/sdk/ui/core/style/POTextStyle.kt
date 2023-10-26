package com.processout.sdk.ui.core.style

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class POTextStyle(
    @ColorRes
    val colorResId: Int,
    val type: POType
) : Parcelable
