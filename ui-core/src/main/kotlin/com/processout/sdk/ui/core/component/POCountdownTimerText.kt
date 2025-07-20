@file:Suppress("DefaultLocale")

package com.processout.sdk.ui.core.component

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.typography
import kotlinx.coroutines.delay

@Composable
fun POCountdownTimerText(
    textFormat: String,
    timeoutSeconds: Int,
    modifier: Modifier = Modifier,
    style: POText.Style = POText.Style(
        color = colors.text.primary,
        textStyle = typography.s15(FontWeight.Medium)
    )
) {
    var secondsLeft by remember { mutableIntStateOf(timeoutSeconds) }
    val formattedText = remember(secondsLeft) {
        val minutes = secondsLeft / 60
        val seconds = secondsLeft % 60
        val formattedTime = String.format("%02d:%02d", minutes, seconds)
        String.format(textFormat, formattedTime)
    }
    LaunchedEffect(secondsLeft) {
        if (secondsLeft > 0) {
            delay(timeMillis = 1000)
            secondsLeft -= 1
        }
    }
    POText(
        text = formattedText,
        modifier = modifier,
        color = style.color,
        style = style.textStyle
    )
}
