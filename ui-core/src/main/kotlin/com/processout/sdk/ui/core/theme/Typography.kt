package com.processout.sdk.ui.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.processout.sdk.ui.core.R
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.style.POTextType
import com.processout.sdk.ui.core.style.POTextType.Weight.*

private val WorkSans = FontFamily(
    Font(R.font.work_sans_regular, FontWeight.Normal),
    Font(R.font.work_sans_medium, FontWeight.Medium),
    Font(R.font.work_sans_semibold, FontWeight.SemiBold)
)

/** @suppress */
@ProcessOutInternalApi
@Immutable
data class POTypography(
    val title: TextStyle = TextStyle(
        fontFamily = WorkSans,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    val largeTitle: TextStyle = TextStyle(
        fontFamily = WorkSans,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 32.sp
    ),
    val subheading: TextStyle = TextStyle(
        fontFamily = WorkSans,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    val body1: TextStyle = TextStyle(
        fontFamily = WorkSans,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    val body2: TextStyle = TextStyle(
        fontFamily = WorkSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    val body3: TextStyle = TextStyle(
        fontFamily = WorkSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    val button: TextStyle = TextStyle(
        fontFamily = WorkSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    val label1: TextStyle = TextStyle(
        fontFamily = WorkSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    val label2: TextStyle = TextStyle(
        fontFamily = WorkSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp
    )
) {

    fun s12(fontWeight: FontWeight = FontWeight.Normal) =
        TextStyle(
            fontFamily = WorkSans,
            fontWeight = fontWeight,
            fontSize = 12.sp,
            lineHeight = 14.sp
        )

    fun s13(fontWeight: FontWeight = FontWeight.Normal) =
        TextStyle(
            fontFamily = WorkSans,
            fontWeight = fontWeight,
            fontSize = 13.sp,
            lineHeight = 16.sp
        )

    fun s14(fontWeight: FontWeight = FontWeight.Normal) =
        TextStyle(
            fontFamily = WorkSans,
            fontWeight = fontWeight,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

    fun s15(fontWeight: FontWeight = FontWeight.Normal) =
        TextStyle(
            fontFamily = WorkSans,
            fontWeight = fontWeight,
            fontSize = 15.sp,
            lineHeight = 18.sp
        )

    fun s16(fontWeight: FontWeight = FontWeight.Normal) =
        TextStyle(
            fontFamily = WorkSans,
            fontWeight = fontWeight,
            fontSize = 16.sp,
            lineHeight = 20.sp
        )

    fun s18(fontWeight: FontWeight = FontWeight.Normal) =
        TextStyle(
            fontFamily = WorkSans,
            fontWeight = fontWeight,
            fontSize = 18.sp,
            lineHeight = 22.sp
        )

    fun s20(fontWeight: FontWeight = FontWeight.Normal) =
        TextStyle(
            fontFamily = WorkSans,
            fontWeight = fontWeight,
            fontSize = 20.sp,
            lineHeight = 24.sp
        )

    fun s24(fontWeight: FontWeight = FontWeight.Normal) =
        TextStyle(
            fontFamily = WorkSans,
            fontWeight = fontWeight,
            fontSize = 24.sp,
            lineHeight = 28.sp
        )
}

internal val LocalPOTypography = staticCompositionLocalOf { POTypography() }

internal fun POTextType.toTextStyle() = TextStyle(
    fontFamily = fontResId?.let { FontFamily(Font(it)) } ?: WorkSans,
    fontWeight = weight.toFontWeight(),
    fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
    fontSize = textSizeSp.sp,
    lineHeight = lineHeightSp.sp
)

private fun POTextType.Weight.toFontWeight(): FontWeight =
    when (this) {
        THIN -> FontWeight.Thin
        EXTRA_LIGHT -> FontWeight.ExtraLight
        LIGHT -> FontWeight.Light
        NORMAL -> FontWeight.Normal
        MEDIUM -> FontWeight.Medium
        SEMI_BOLD -> FontWeight.SemiBold
        BOLD -> FontWeight.Bold
        EXTRA_BOLD -> FontWeight.ExtraBold
        BLACK -> FontWeight.Black
    }
