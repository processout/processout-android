package com.processout.sdk.ui.core.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
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
    title: String?,
    modifier: Modifier = Modifier,
    style: POText.Style = POText.title,
    dividerColor: Color = ProcessOutTheme.colors.border.subtle,
    dragHandleColor: Color = ProcessOutTheme.colors.border.disabled,
    withDragHandle: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (withDragHandle) {
            PODragHandle(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                color = dragHandleColor
            )
        }
        if (!title.isNullOrBlank()) {
            POText(
                text = title,
                modifier = Modifier.padding(titlePadding(withDragHandle)),
                color = style.color,
                style = style.textStyle
            )
            HorizontalDivider(thickness = 1.dp, color = dividerColor)
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
        top = spacing.small,
        bottom = spacing.large
    ) else PaddingValues(
        horizontal = spacing.extraLarge,
        vertical = spacing.large
    )
}
