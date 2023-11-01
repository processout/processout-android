package com.processout.sdk.ui.core.component.field

import androidx.compose.runtime.Immutable
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POText

/** @suppress */
@ProcessOutInternalApi
@Immutable
data class POFieldLabelsStyle(
    val normal: POFieldLabelsStateStyle,
    val error: POFieldLabelsStateStyle
)

/** @suppress */
@ProcessOutInternalApi
@Immutable
data class POFieldLabelsStateStyle(
    val title: POText.Style,
    val description: POText.Style
)
