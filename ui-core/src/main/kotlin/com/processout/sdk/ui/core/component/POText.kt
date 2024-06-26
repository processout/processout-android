package com.processout.sdk.ui.core.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
    style: TextStyle = ProcessOutTheme.typography.body2,
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

    val body: Style
        @Composable get() = Style(
            color = ProcessOutTheme.colors.text.primary,
            textStyle = ProcessOutTheme.typography.body2
        )

    val label1: Style
        @Composable get() = Style(
            color = ProcessOutTheme.colors.text.secondary,
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
}

@Composable
@Preview(showBackground = true)
private fun POTextPreview() {
    POText(text = "ProcessOut Payment")
}
