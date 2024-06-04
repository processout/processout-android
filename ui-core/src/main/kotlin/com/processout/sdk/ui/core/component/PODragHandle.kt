package com.processout.sdk.ui.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.PODragHandle.DragHandleHeight
import com.processout.sdk.ui.core.component.PODragHandle.DragHandleWidth
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
            .padding(
                top = spacing.medium,
                bottom = spacing.medium
            )
            .requiredSize(
                width = DragHandleWidth,
                height = DragHandleHeight
            )
            .background(
                color = color,
                shape = shapes.roundedCornersSmall
            )
    )
}

internal object PODragHandle {
    val DragHandleWidth = 32.dp
    val DragHandleHeight = 4.dp
}
