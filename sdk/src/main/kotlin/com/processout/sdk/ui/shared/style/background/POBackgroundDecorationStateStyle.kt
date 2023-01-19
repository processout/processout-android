package com.processout.sdk.ui.shared.style.background

import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize

sealed class POBackgroundDecorationStateStyle : Parcelable {
    @Parcelize
    data class Visible(
        @ColorInt
        val primaryColor: Int,
        @ColorInt
        val secondaryColor: Int
    ) : POBackgroundDecorationStateStyle()

    @Parcelize
    object Hidden : POBackgroundDecorationStateStyle()
}
