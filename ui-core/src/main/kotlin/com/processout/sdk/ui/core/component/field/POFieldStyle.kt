package com.processout.sdk.ui.core.component.field

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POBorderStroke
import com.processout.sdk.ui.core.component.POText

/** @suppress */
@ProcessOutInternalApi
@Immutable
data class POFieldStyle(
    val normal: POFieldStateStyle,
    val error: POFieldStateStyle
)

/** @suppress */
@ProcessOutInternalApi
@Immutable
data class POFieldStateStyle(
    val text: POText.Style,
    val hintTextColor: Color,
    val backgroundColor: Color,
    val controlsTintColor: Color,
    val shape: Shape,
    val border: POBorderStroke
)
