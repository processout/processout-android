@file:Suppress("NAME_SHADOWING")

package com.processout.sdk.ui.shared.composable

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.shared.extension.conditional

@Composable
internal fun ExpandableText(
    text: String?,
    style: POText.Style,
    modifier: Modifier = Modifier
) {
    val modifier = modifier
        .animateContentSize()
        .conditional(
            condition = text != null,
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
