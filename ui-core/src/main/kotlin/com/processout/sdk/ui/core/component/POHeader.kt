package com.processout.sdk.ui.core.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
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
    title: String,
    style: POText.Style = POText.title,
    dividerColor: Color = ProcessOutTheme.colors.border.subtle,
    dragHandleColor: Color = ProcessOutTheme.colors.border.disabled,
    withDragHandle: Boolean = true
) {
    Column {
        if (withDragHandle) {
            PODragHandle(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                color = dragHandleColor
            )
        }
        POText(
            text = title,
            modifier = Modifier.padding(titlePadding(withDragHandle)),
            color = style.color,
            style = style.textStyle
        )
        Divider(thickness = 1.dp, color = dividerColor)
    }
}

@Composable
private fun titlePadding(
    withDragHandle: Boolean
): PaddingValues = if (withDragHandle) PaddingValues(
    start = ProcessOutTheme.spacing.extraLarge,
    top = ProcessOutTheme.spacing.medium,
    end = ProcessOutTheme.spacing.extraLarge,
    bottom = ProcessOutTheme.spacing.large
) else PaddingValues(
    horizontal = ProcessOutTheme.spacing.extraLarge,
    vertical = ProcessOutTheme.spacing.large
)
