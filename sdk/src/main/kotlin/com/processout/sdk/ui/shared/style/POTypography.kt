package com.processout.sdk.ui.shared.style

import android.os.Parcelable
import androidx.annotation.FontRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class POTypography(
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

    companion object {
        val titleLarge = POTypography(weight = Weight.NORMAL, textSizeSp = 24, lineHeightSp = 32)

        val titleDefault = POTypography(weight = Weight.NORMAL, textSizeSp = 20, lineHeightSp = 24)

        val bodyDefault = POTypography(weight = Weight.NORMAL, textSizeSp = 16, lineHeightSp = 24)

        val bodyDefaultMedium = POTypography(weight = Weight.MEDIUM, textSizeSp = 16, lineHeightSp = 24)

        val bodySmall = POTypography(weight = Weight.NORMAL, textSizeSp = 12, lineHeightSp = 16)

        val bodySmallMedium = POTypography(weight = Weight.MEDIUM, textSizeSp = 12, lineHeightSp = 16)

        val inputLabel = POTypography(weight = Weight.NORMAL, textSizeSp = 16, lineHeightSp = 24)

        val inputString = POTypography(weight = Weight.NORMAL, textSizeSp = 16, lineHeightSp = 24)

        val actionDefault = POTypography(weight = Weight.NORMAL, textSizeSp = 16, lineHeightSp = 24)

        val actionDefaultMedium = POTypography(weight = Weight.MEDIUM, textSizeSp = 16, lineHeightSp = 24)
    }
}
