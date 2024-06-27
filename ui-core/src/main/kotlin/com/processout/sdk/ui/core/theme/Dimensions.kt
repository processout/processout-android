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
    val formComponentMinSize: Dp = 48.dp
)

internal val LocalPODimensions = staticCompositionLocalOf { PODimensions() }
