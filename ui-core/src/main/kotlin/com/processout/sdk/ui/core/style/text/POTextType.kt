package com.processout.sdk.ui.core.style.text

import android.os.Parcelable
import androidx.annotation.FontRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class POTextType(
    @FontRes
    val fontResId: Int? = null,
    val weight: Weight,
    val italic: Boolean = false,
    val textSizeSp: Int,
    val lineHeightSp: Int
) : Parcelable {

    @Parcelize
    enum class Weight(val value: Int) : Parcelable {
        THIN(100),
        EXTRA_LIGHT(200),
        LIGHT(300),
        NORMAL(400),
        MEDIUM(500),
        SEMI_BOLD(600),
        BOLD(700),
        EXTRA_BOLD(800),
        BLACK(900)
    }

    object Fixed {
        val body = POTextType(weight = Weight.NORMAL, textSizeSp = 16, lineHeightSp = 24)
        val bodyCompact = POTextType(weight = Weight.NORMAL, textSizeSp = 16, lineHeightSp = 20)
        val label = POTextType(weight = Weight.NORMAL, textSizeSp = 14, lineHeightSp = 18)
        val labelHeading = POTextType(weight = Weight.MEDIUM, textSizeSp = 14, lineHeightSp = 18)
        val button = POTextType(weight = Weight.MEDIUM, textSizeSp = 14, lineHeightSp = 14)
        val caption = POTextType(weight = Weight.NORMAL, textSizeSp = 12, lineHeightSp = 16)
    }

    object Medium {
        val title = POTextType(weight = Weight.MEDIUM, textSizeSp = 20, lineHeightSp = 28)
        val subtitle = POTextType(weight = Weight.MEDIUM, textSizeSp = 18, lineHeightSp = 24)
    }
}
