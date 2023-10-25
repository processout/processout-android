package com.processout.sdk.ui.core.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
@Immutable
data class POBorderStroke(
    val width: Dp,
    val color: Color
)

internal fun POBorderStroke.solid(
    width: Dp = this.width,
    color: Color = this.color
) = BorderStroke(width = width, color = color)
