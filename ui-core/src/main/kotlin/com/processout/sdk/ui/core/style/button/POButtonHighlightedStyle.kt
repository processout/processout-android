package com.processout.sdk.ui.core.style.button

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class POButtonHighlightedStyle(
    @ColorRes
    val textColorResId: Int,
    @ColorRes
    val borderColorResId: Int,
    @ColorRes
    val backgroundColorResId: Int
) : Parcelable
