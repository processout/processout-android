package com.processout.sdk.ui.core.style

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class POGroupedContentStyle(
    val title: POTextStyle,
    val border: POBorderStyle,
    @ColorRes
    val dividerColorResId: Int,
    @ColorRes
    val backgroundColorResId: Int
) : Parcelable
