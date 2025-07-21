package com.processout.sdk.ui.core.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.extension.conditional
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POHeader(
    title: String?,
    modifier: Modifier = Modifier,
    style: POText.Style = POText.title,
    dividerColor: Color = colors.border.border4,
    dragHandleColor: Color = colors.icon.disabled,
    withDragHandle: Boolean = true,
    animationDurationMillis: Int = 0,
    trailingContent: @Composable RowScope.() -> Unit = {}
) {
    Box(modifier = modifier.fillMaxWidth()) {
        if (withDragHandle) {
            PODragHandle(
                modifier = Modifier
                    .padding(top = spacing.medium)
                    .align(alignment = Alignment.TopCenter),
                color = dragHandleColor
            )
        }
        AnimatedVisibility(
            visible = !title.isNullOrBlank(),
            enter = fadeIn(animationSpec = tween(durationMillis = animationDurationMillis)),
            exit = fadeOut(animationSpec = tween(durationMillis = animationDurationMillis)),
        ) {
            Column(
                modifier = Modifier.conditional(withDragHandle) {
                    padding(top = spacing.small)
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var currentTitle by remember { mutableStateOf(String()) }
                    if (!title.isNullOrBlank()) {
                        currentTitle = title
                    }
                    POText(
                        text = currentTitle,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(spacing.extraLarge),
                        color = style.color,
                        style = style.textStyle
                    )
                    trailingContent()
                }
                HorizontalDivider(thickness = 1.dp, color = dividerColor)
            }
        }
    }
}
