package com.processout.sdk.ui.core.component

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POExpandableText(
    text: String?,
    style: POText.Style,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null
) {
    AnimatedVisibility(
        visible = !text.isNullOrBlank(),
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        POText(
            text = text ?: String(),
            modifier = modifier,
            color = style.color,
            style = style.textStyle,
            textAlign = textAlign
        )
    }
}
