package com.processout.sdk.ui.core.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.style.POStrokeStyle

/** @suppress */
@ProcessOutInternalApi
object POStroke {

    @Immutable
    data class Style(
        val width: Dp,
        val color: Color,
        val dashInterval: Dp? = null
    )

    @Composable
    fun custom(style: POStrokeStyle) = Style(
        width = style.widthDp.dp,
        color = colorResource(id = style.colorResId),
        dashInterval = style.dashIntervalDp?.dp
    )
}
