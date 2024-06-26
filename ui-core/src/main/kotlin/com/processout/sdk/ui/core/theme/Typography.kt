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
    Font(R.font.work_sans_medium, FontWeight.Medium)
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
    ),
    val fixed: Fixed = Fixed()
) {
    @Immutable
    data class Fixed(
        val body: TextStyle = TextStyle(
            fontFamily = WorkSans,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp
        ),
        val label: TextStyle = TextStyle(
            fontFamily = WorkSans,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 18.sp
        ),
        val labelHeading: TextStyle = TextStyle(
            fontFamily = WorkSans,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 18.sp
        )
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
