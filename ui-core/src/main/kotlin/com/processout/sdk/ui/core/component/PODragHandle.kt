package com.processout.sdk.ui.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes

/** @suppress */
@ProcessOutInternalApi
@Composable
fun PODragHandle(
    modifier: Modifier = Modifier,
    color: Color = colors.border.subtle
) {
    Box(
        modifier = modifier
            .requiredSize(
                width = 32.dp,
                height = 4.dp
            )
            .background(
                color = color,
                shape = shapes.roundedCornersSmall
            )
    )
}
