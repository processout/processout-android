package com.processout.sdk.ui.core.component

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.theme.ProcessOutTheme

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POTextAutoSize(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = ProcessOutTheme.typography.body1,
    fontStyle: FontStyle? = null,
    textAlign: TextAlign? = null
) {
    var resizedStyle by remember { mutableStateOf(style) }
    var readyToDraw by remember { mutableStateOf(false) }
    POText(
        text = text,
        modifier = modifier.drawWithContent {
            if (readyToDraw) {
                drawContent()
            }
        },
        color = color,
        style = resizedStyle,
        fontStyle = fontStyle,
        textAlign = textAlign,
        onTextLayout = { result ->
            if (result.didOverflowWidth) {
                resizedStyle = resizedStyle.copy(
                    fontSize = resizedStyle.fontSize * 0.98,
                    lineHeight = resizedStyle.lineHeight * 0.98
                )
            } else {
                readyToDraw = true
            }
        },
        softWrap = false,
        maxLines = 1
    )
}
