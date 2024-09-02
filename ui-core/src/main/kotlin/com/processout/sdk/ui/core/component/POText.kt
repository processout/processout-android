package com.processout.sdk.ui.core.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.style.POTextStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.core.theme.toTextStyle

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = ProcessOutTheme.typography.body1,
    fontStyle: FontStyle? = null,
    textAlign: TextAlign? = null,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1
) = Text(
    text = text,
    modifier = modifier,
    color = color,
    style = style,
    fontStyle = fontStyle,
    textAlign = textAlign,
    onTextLayout = onTextLayout,
    overflow = overflow,
    softWrap = softWrap,
    maxLines = maxLines,
    minLines = minLines
)

/** @suppress */
@ProcessOutInternalApi
object POText {

    @Immutable
    data class Style(
        val color: Color,
        val textStyle: TextStyle
    )

    val title: Style
        @Composable get() = Style(
            color = ProcessOutTheme.colors.text.primary,
            textStyle = ProcessOutTheme.typography.title
        )

    val subheading: Style
        @Composable get() = Style(
            color = ProcessOutTheme.colors.text.primary,
            textStyle = ProcessOutTheme.typography.subheading
        )

    val body1: Style
        @Composable get() = Style(
            color = ProcessOutTheme.colors.text.primary,
            textStyle = ProcessOutTheme.typography.body1
        )

    val body2: Style
        @Composable get() = Style(
            color = ProcessOutTheme.colors.text.primary,
            textStyle = ProcessOutTheme.typography.body2
        )

    val label1: Style
        @Composable get() = Style(
            color = ProcessOutTheme.colors.text.primary,
            textStyle = ProcessOutTheme.typography.label1
        )

    val errorLabel: Style
        @Composable get() = Style(
            color = ProcessOutTheme.colors.text.error,
            textStyle = ProcessOutTheme.typography.label2
        )

    @Composable
    fun custom(style: POTextStyle) = Style(
        color = colorResource(id = style.colorResId),
        textStyle = style.type.toTextStyle()
    )

    @Composable
    fun measuredPaddingTop(
        textStyle: TextStyle,
        componentHeight: Dp
    ): Dp {
        val textMeasurer = rememberTextMeasurer()
        val singleLineTextMeasurement = remember(textStyle) {
            textMeasurer.measure(text = String(), style = textStyle)
        }
        val density = LocalDensity.current
        return remember(singleLineTextMeasurement, componentHeight) {
            with(density) {
                val componentCenterHeight = componentHeight / 2
                val singleLineTextCenterHeight = singleLineTextMeasurement.size.height.toDp() / 2
                val paddingTop = componentCenterHeight - singleLineTextCenterHeight
                if (paddingTop.value > 0) paddingTop else 0.dp
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun POTextPreview() {
    POText(text = "ProcessOut Payment")
}
