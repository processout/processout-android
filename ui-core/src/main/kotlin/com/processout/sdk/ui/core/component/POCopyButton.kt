@file:Suppress("MayBeConstant")

package com.processout.sdk.ui.core.component

import android.content.ClipData
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.processout.sdk.ui.core.R
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POCopyButton.FadeAnimationDurationMillis
import com.processout.sdk.ui.core.component.POCopyButton.SizeAnimationDurationMillis
import com.processout.sdk.ui.core.shared.image.PODrawableImage
import com.processout.sdk.ui.core.shared.image.POImageRenderingMode
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.core.theme.ProcessOutTheme.typography
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
    val clipboard = LocalClipboard.current
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
                coroutineScope.launch {
                    clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(textToCopy, textToCopy)))
                    isCopied = true
                    timerJob?.cancel()
                    timerJob = launch {
                        delay(timeMillis = 2500)
                        isCopied = false
                    }
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
        @Composable get() = POButton.secondary.let {
            it.copy(
                normal = it.normal.copy(
                    text = it.normal.text.copy(
                        textStyle = typography.s13(FontWeight.Medium)
                    ),
                    paddingHorizontal = spacing.space10
                ),
                disabled = it.disabled.copy(
                    text = it.disabled.text.copy(
                        textStyle = typography.s13(FontWeight.Medium)
                    ),
                    paddingHorizontal = spacing.space10
                )
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
