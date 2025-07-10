package com.processout.sdk.ui.core.style

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class POStrokeStyle(
    val widthDp: Int,
    @ColorRes
    val colorResId: Int,
    val dashIntervalDp: Int? = null
) : Parcelable
