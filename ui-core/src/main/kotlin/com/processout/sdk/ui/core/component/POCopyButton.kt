package com.processout.sdk.ui.core.component

import androidx.compose.animation.animateContentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.shared.image.PODrawableImage
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POCopyButton(
    textToCopy: String,
    copyText: String,
    copiedText: String,
    modifier: Modifier = Modifier,
    style: POButton.Style = POCopyButton.default,
    copyIcon: PODrawableImage? = null,
    copiedIcon: PODrawableImage? = null,
    iconSize: Dp = dimensions.iconSizeSmall
) {
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var timerJob by remember { mutableStateOf<Job?>(null) }
    POButton(
        text = if (isCopied) copiedText else copyText,
        onClick = {
            clipboardManager.setText(AnnotatedString(textToCopy))
            isCopied = true
            timerJob?.cancel()
            timerJob = coroutineScope.launch {
                delay(timeMillis = 3000)
                isCopied = false
            }
        },
        modifier = modifier.animateContentSize(),
        style = style,
        icon = if (isCopied) copiedIcon else copyIcon,
        iconSize = iconSize
    )
}

/** @suppress */
@ProcessOutInternalApi
object POCopyButton {

    val default: POButton.Style
        @Composable get() = POButton.secondary2.let {
            it.copy(
                normal = it.normal.copy(paddingHorizontal = spacing.space12),
                disabled = it.disabled.copy(paddingHorizontal = spacing.space12)
            )
        }
}
