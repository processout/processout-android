@file:Suppress("DefaultLocale")

package com.processout.sdk.ui.core.component

import android.os.SystemClock
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.typography
import kotlinx.coroutines.delay

/** @suppress */
@ProcessOutInternalApi
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
    val startTimeMillis = remember { SystemClock.elapsedRealtime() }
    var remainingSeconds by remember { mutableIntStateOf(timeoutSeconds) }
    val formattedText = remember(remainingSeconds) {
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60
        val formattedTime = String.format("%02d:%02d", minutes, seconds)
        String.format(textFormat, formattedTime)
    }
    LaunchedEffect(Unit) {
        while (remainingSeconds > 0) {
            delay(timeMillis = 1000)
            val elapsedSeconds = ((SystemClock.elapsedRealtime() - startTimeMillis) / 1000L).toInt()
            remainingSeconds = (timeoutSeconds - elapsedSeconds).coerceAtLeast(minimumValue = 0)
        }
    }
    POText(
        text = formattedText,
        modifier = modifier,
        color = style.color,
        style = style.textStyle
    )
}
