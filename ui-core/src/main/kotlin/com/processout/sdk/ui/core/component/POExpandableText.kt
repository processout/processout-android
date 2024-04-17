@file:Suppress("NAME_SHADOWING")

package com.processout.sdk.ui.core.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.extension.conditional

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POExpandableText(
    text: String?,
    style: POText.Style,
    modifier: Modifier = Modifier
) {
    val modifier = modifier
        .animateContentSize()
        .conditional(
            condition = !text.isNullOrBlank(),
            whenTrue = { wrapContentHeight() },
            whenFalse = { requiredHeight(0.dp) }
        )
    with(style) {
        POText(
            text = text ?: String(),
            modifier = modifier,
            color = color,
            style = textStyle
        )
    }
}
