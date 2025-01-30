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
    val formComponentMinHeight: Dp = 48.dp,
    val interactiveComponentMinSize: Dp = 44.dp,
    val iconSizeSmall: Dp = 16.dp,
    val iconSizeMedium: Dp = 20.dp,
    val buttonIconSizeSmall: Dp = iconSizeSmall * 2,
    val buttonIconSizeMedium: Dp = iconSizeMedium * 2
)

internal val LocalPODimensions = staticCompositionLocalOf { PODimensions() }
