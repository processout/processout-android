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

    object Fixed {
        val caption = POTypography(weight = Weight.NORMAL, textSizeSp = 12, lineHeightSp = 16)

        val tag = POTypography(weight = Weight.MEDIUM, textSizeSp = 12, lineHeightSp = 16)

        val button = POTypography(weight = Weight.MEDIUM, textSizeSp = 14, lineHeightSp = 14)

        val tabular = POTypography(weight = Weight.NORMAL, textSizeSp = 14, lineHeightSp = 20)

        val body = POTypography(weight = Weight.NORMAL, textSizeSp = 16, lineHeightSp = 24)

        val label = POTypography(weight = Weight.NORMAL, textSizeSp = 14, lineHeightSp = 18)

        val labelHeading = POTypography(weight = Weight.MEDIUM, textSizeSp = 14, lineHeightSp = 18)
    }

    object Medium {
        val subtitle = POTypography(weight = Weight.MEDIUM, textSizeSp = 18, lineHeightSp = 24)

        val title = POTypography(weight = Weight.MEDIUM, textSizeSp = 20, lineHeightSp = 28)

        val headline = POTypography(weight = Weight.MEDIUM, textSizeSp = 24, lineHeightSp = 32)

        val display = POTypography(weight = Weight.MEDIUM, textSizeSp = 36, lineHeightSp = 44)
    }

    object Large {
        val subtitle = POTypography(weight = Weight.MEDIUM, textSizeSp = 20, lineHeightSp = 28)

        val title = POTypography(weight = Weight.MEDIUM, textSizeSp = 24, lineHeightSp = 32)

        val headline = POTypography(weight = Weight.MEDIUM, textSizeSp = 32, lineHeightSp = 40)

        val display = POTypography(weight = Weight.MEDIUM, textSizeSp = 48, lineHeightSp = 48)
    }

    companion object {
        @Deprecated(message = "Use .Fixed .Medium .Large")
        val titleLarge = POTypography(weight = Weight.NORMAL, textSizeSp = 24, lineHeightSp = 32)

        @Deprecated(message = "Use .Fixed .Medium .Large")
        val titleDefault = POTypography(weight = Weight.NORMAL, textSizeSp = 20, lineHeightSp = 24)

        @Deprecated(message = "Use .Fixed .Medium .Large")
        val bodyDefault = POTypography(weight = Weight.NORMAL, textSizeSp = 16, lineHeightSp = 24)

        @Deprecated(message = "Use .Fixed .Medium .Large")
        val bodyDefaultMedium = POTypography(weight = Weight.MEDIUM, textSizeSp = 16, lineHeightSp = 24)

        @Deprecated(message = "Use .Fixed .Medium .Large")
        val bodySmall = POTypography(weight = Weight.NORMAL, textSizeSp = 12, lineHeightSp = 16)

        @Deprecated(message = "Use .Fixed .Medium .Large")
        val bodySmallMedium = POTypography(weight = Weight.MEDIUM, textSizeSp = 12, lineHeightSp = 16)

        @Deprecated(message = "Use .Fixed .Medium .Large")
        val inputLabel = POTypography(weight = Weight.NORMAL, textSizeSp = 16, lineHeightSp = 24)

        @Deprecated(message = "Use .Fixed .Medium .Large")
        val inputString = POTypography(weight = Weight.NORMAL, textSizeSp = 16, lineHeightSp = 24)

        @Deprecated(message = "Use .Fixed .Medium .Large")
        val actionDefault = POTypography(weight = Weight.NORMAL, textSizeSp = 16, lineHeightSp = 24)

        @Deprecated(message = "Use .Fixed .Medium .Large")
        val actionDefaultMedium = POTypography(weight = Weight.MEDIUM, textSizeSp = 16, lineHeightSp = 24)
    }
}
