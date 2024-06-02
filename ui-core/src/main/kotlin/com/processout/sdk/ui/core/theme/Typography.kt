package com.processout.sdk.ui.core.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    val fixed: Fixed = Fixed(),
    val medium: Medium = Medium()
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
        ),
        val button: TextStyle = TextStyle(
            fontFamily = WorkSans,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 14.sp
        ),
        val caption: TextStyle = TextStyle(
            fontFamily = WorkSans,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    )

    @Immutable
    data class Medium(
        val title: TextStyle = TextStyle(
            fontFamily = WorkSans,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            lineHeight = 28.sp
        ),
        val subtitle: TextStyle = TextStyle(
            fontFamily = WorkSans,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            lineHeight = 24.sp
        ),
        val body: TextStyle = TextStyle(
            fontFamily = WorkSans,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp
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

@Composable
@Preview(showBackground = true)
private fun POTypographyPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Typography Medium Title",
            style = LocalPOTypography.current.medium.title
        )
        Text(
            text = "Typography Medium Subtitle",
            style = LocalPOTypography.current.medium.subtitle
        )
        Text(
            text = "Typography Fixed Body",
            style = LocalPOTypography.current.fixed.body
        )
        Text(
            text = "Typography Fixed Label",
            style = LocalPOTypography.current.fixed.label
        )
        Text(
            text = "Typography Fixed Label Heading",
            style = LocalPOTypography.current.fixed.labelHeading
        )
        Text(
            text = "Typography Fixed Button",
            style = LocalPOTypography.current.fixed.button
        )
        Text(
            text = "Typography Fixed Caption",
            style = LocalPOTypography.current.fixed.caption
        )
    }
}
