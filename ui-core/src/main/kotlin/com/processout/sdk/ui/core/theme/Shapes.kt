package com.processout.sdk.ui.core.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
@Immutable
data class POShapes(
    val roundedCorners6: CornerBasedShape = RoundedCornerShape(6.dp),
    val roundedCorners8: CornerBasedShape = RoundedCornerShape(8.dp),
    val roundedCornersSmall: CornerBasedShape = RoundedCornerShape(4.dp),
    val roundedCornersMedium: CornerBasedShape = RoundedCornerShape(8.dp),
    val roundedCornersLarge: CornerBasedShape = RoundedCornerShape(16.dp),
    val topRoundedCornersLarge: CornerBasedShape = RoundedCornerShape(
        topStart = 16.dp, topEnd = 16.dp
    )
)

internal val LocalPOShapes = staticCompositionLocalOf { POShapes() }
