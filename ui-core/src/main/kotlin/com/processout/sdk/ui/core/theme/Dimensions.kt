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
    val paddingSmall: Dp = 8.dp,
    val paddingMedium: Dp = 16.dp,
    val paddingLarge: Dp = 24.dp,
    val formComponentHeight: Dp = 44.dp
)

internal val LocalPODimensions = staticCompositionLocalOf { PODimensions() }
