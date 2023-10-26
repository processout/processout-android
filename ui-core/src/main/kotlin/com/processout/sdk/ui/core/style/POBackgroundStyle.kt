package com.processout.sdk.ui.core.style

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class POBackgroundStyle(
    @ColorRes
    val normal: Int,
    @ColorRes
    val success: Int
) : Parcelable
