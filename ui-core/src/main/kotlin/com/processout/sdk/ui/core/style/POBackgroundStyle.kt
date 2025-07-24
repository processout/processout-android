package com.processout.sdk.ui.core.style

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize

@Deprecated(message = "Only used in deprecated implementation.")
@Parcelize
data class POBackgroundStyle(
    @ColorRes
    val normalColorResId: Int,
    @ColorRes
    val successColorResId: Int
) : Parcelable
