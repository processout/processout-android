package com.processout.sdk.ui.core.style

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class POTextStyle(
    @ColorRes
    val colorResId: Int,
    val type: POTextType
) : Parcelable

@Parcelize
data class POTextType(
    val textSizeSp: Int,
    val lineHeightSp: Int,
    @FontRes
    val fontResId: Int? = null,
    val weight: Weight = Weight.NORMAL,
    val italic: Boolean = false
) : Parcelable {

    @Parcelize
    enum class Weight : Parcelable {
        THIN,
        EXTRA_LIGHT,
        LIGHT,
        NORMAL,
        MEDIUM,
        SEMI_BOLD,
        BOLD,
        EXTRA_BOLD,
        BLACK
    }
}
