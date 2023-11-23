package com.processout.sdk.ui.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.theme.ProcessOutTheme

/** @suppress */
@ProcessOutInternalApi
@Composable
fun PODragHandle(
    modifier: Modifier = Modifier,
    color: Color = ProcessOutTheme.colors.border.disabled
) = with(ProcessOutTheme) {
    Box(
        modifier = modifier
            .padding(top = spacing.medium)
            .size(
                width = dimensions.dragHandleWidth,
                height = dimensions.dragHandleHeight
            )
            .background(
                color = color,
                shape = shapes.roundedCornersSmall
            )
    )
}
