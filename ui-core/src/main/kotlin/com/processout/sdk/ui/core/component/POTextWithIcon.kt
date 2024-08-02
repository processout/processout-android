package com.processout.sdk.ui.core.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POTextWithIcon.paddingValues
import com.processout.sdk.ui.core.theme.ProcessOutTheme

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POTextWithIcon(
    text: String,
    iconPainter: Painter,
    modifier: Modifier = Modifier,
    style: TextStyle = ProcessOutTheme.typography.body1,
    textColor: Color = Color.Unspecified,
    iconColorFilter: ColorFilter? = null,
    fontStyle: FontStyle? = null,
    textAlign: TextAlign? = null,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start
) {
    Row(
        horizontalArrangement = horizontalArrangement
    ) {
        val paddingValues = paddingValues(
            iconPainter = iconPainter,
            textStyle = style
        )
        Image(
            painter = iconPainter,
            contentDescription = null,
            modifier = Modifier.padding(top = paddingValues.iconPaddingTop),
            colorFilter = iconColorFilter
        )
        POText(
            text = text,
            modifier = modifier.padding(top = paddingValues.textPaddingTop),
            color = textColor,
            style = style,
            fontStyle = fontStyle,
            textAlign = textAlign,
            onTextLayout = onTextLayout,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines
        )
    }
}

internal object POTextWithIcon {

    @Immutable
    data class PaddingValues(
        val iconPaddingTop: Dp,
        val textPaddingTop: Dp
    )

    @Composable
    fun paddingValues(
        iconPainter: Painter,
        textStyle: TextStyle
    ): PaddingValues {
        val textMeasurer = rememberTextMeasurer()
        val singleLineTextMeasurement = remember(textStyle) {
            textMeasurer.measure(text = String(), style = textStyle)
        }
        val density = LocalDensity.current
        return remember(iconPainter, singleLineTextMeasurement) {
            with(density) {
                val iconCenterHeight = iconPainter.intrinsicSize.height.toDp() / 2
                val singleLineTextCenterHeight = singleLineTextMeasurement.size.height.toDp() / 2
                val paddingTop = singleLineTextCenterHeight - iconCenterHeight
                PaddingValues(
                    iconPaddingTop = if (paddingTop > 0.dp) paddingTop else 0.dp,
                    textPaddingTop = if (paddingTop < 0.dp) paddingTop.unaryMinus() else 0.dp
                )
            }
        }
    }
}
