package com.processout.sdk.ui.shared.style.button

import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize

@Parcelize
data class POButtonHighlightedStyle(
    @ColorInt
    val textColor: Int,
    @ColorInt
    val borderColor: Int,
    @ColorInt
    val backgroundColor: Int
) : Parcelable
