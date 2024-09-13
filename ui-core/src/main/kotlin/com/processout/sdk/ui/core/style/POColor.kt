package com.processout.sdk.ui.core.style

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class POColor(
    @ColorRes
    val lightColorResId: Int,
    @ColorRes
    val darkColorResId: Int
) : Parcelable
