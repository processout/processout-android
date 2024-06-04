package com.processout.sdk.ui.core.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.theme.ProcessOutTheme

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POHeader(
    title: String?,
    modifier: Modifier = Modifier,
    style: POText.Style = POText.title,
    dividerColor: Color = ProcessOutTheme.colors.border.subtle,
    dragHandleColor: Color = ProcessOutTheme.colors.border.disabled,
    withDragHandle: Boolean = true,
    animationDurationMillis: Int = 0
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (withDragHandle) {
            PODragHandle(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                color = dragHandleColor
            )
        }
        AnimatedVisibility(
            visible = !title.isNullOrBlank(),
            enter = fadeIn(animationSpec = tween(durationMillis = animationDurationMillis)),
            exit = fadeOut(animationSpec = tween(durationMillis = animationDurationMillis)),
        ) {
            Column {
                var currentTitle by remember { mutableStateOf(String()) }
                if (!title.isNullOrBlank()) {
                    currentTitle = title
                }
                POText(
                    text = currentTitle,
                    modifier = Modifier.padding(titlePadding(withDragHandle)),
                    color = style.color,
                    style = style.textStyle
                )
                HorizontalDivider(thickness = 1.dp, color = dividerColor)
            }
        }
    }
}

@Composable
private fun titlePadding(
    withDragHandle: Boolean
): PaddingValues = with(ProcessOutTheme) {
    if (withDragHandle) PaddingValues(
        start = spacing.extraLarge,
        end = spacing.extraLarge,
        top = 0.dp,
        bottom = spacing.large
    ) else PaddingValues(
        horizontal = spacing.extraLarge,
        vertical = spacing.large
    )
}
