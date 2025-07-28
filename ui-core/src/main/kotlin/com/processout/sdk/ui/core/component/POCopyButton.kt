@file:Suppress("MayBeConstant")

package com.processout.sdk.ui.core.component

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp
import com.processout.sdk.ui.core.R
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POCopyButton.FadeAnimationDurationMillis
import com.processout.sdk.ui.core.component.POCopyButton.SizeAnimationDurationMillis
import com.processout.sdk.ui.core.shared.image.PODrawableImage
import com.processout.sdk.ui.core.shared.image.POImageRenderingMode
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
    copyIcon: PODrawableImage? = POCopyButton.CopyIcon,
    copiedIcon: PODrawableImage? = POCopyButton.CopiedIcon,
    iconSize: Dp = dimensions.iconSizeSmall
) {
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }
    var timerJob by remember { mutableStateOf<Job?>(null) }
    val coroutineScope = rememberCoroutineScope()
    AnimatedContent(
        targetState = isCopied,
        transitionSpec = {
            fadeIn(
                tween(durationMillis = FadeAnimationDurationMillis)
            ) togetherWith fadeOut(
                tween(durationMillis = FadeAnimationDurationMillis)
            ) using SizeTransform { _, _ ->
                tween(durationMillis = SizeAnimationDurationMillis)
            }
        }
    ) { isCopiedAnimated ->
        POButton(
            text = if (isCopiedAnimated) copiedText else copyText,
            onClick = {
                clipboardManager.setText(AnnotatedString(textToCopy))
                isCopied = true
                timerJob?.cancel()
                timerJob = coroutineScope.launch {
                    delay(timeMillis = 3000)
                    isCopied = false
                }
            },
            modifier = modifier,
            style = style,
            icon = if (isCopiedAnimated) copiedIcon else copyIcon,
            iconSize = iconSize
        )
    }
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

    internal val CopyIcon = PODrawableImage(
        resId = R.drawable.po_icon_copy,
        renderingMode = POImageRenderingMode.TEMPLATE
    )

    internal val CopiedIcon = PODrawableImage(
        resId = R.drawable.po_icon_check,
        renderingMode = POImageRenderingMode.TEMPLATE
    )

    internal val FadeAnimationDurationMillis = 300
    internal val SizeAnimationDurationMillis = 400
}
