package com.processout.sdk.ui.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
@Immutable
data class PODimensions(
    val formComponentHeight: Dp = 44.dp,
    val dragHandleWidth: Dp = 32.dp,
    val dragHandleHeight: Dp = 4.dp
)

internal val LocalPODimensions = staticCompositionLocalOf { PODimensions() }
