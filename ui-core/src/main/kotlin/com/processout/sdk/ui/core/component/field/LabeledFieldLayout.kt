package com.processout.sdk.ui.core.component.field

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.processout.sdk.ui.core.component.POExpandableText
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing

@Composable
internal fun LabeledFieldLayout(
    title: String,
    description: String?,
    style: POFieldLabels.Style = POFieldLabels.default,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = horizontalAlignment
    ) {
        POText(
            text = title,
            modifier = Modifier.padding(bottom = spacing.small),
            color = style.title.color,
            style = style.title.textStyle,
            textAlign = textAlign(horizontalAlignment)
        )
        content()
        POExpandableText(
            text = description,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = spacing.small),
            style = style.description,
            textAlign = textAlign(horizontalAlignment)
        )
    }
}

private fun textAlign(
    horizontalAlignment: Alignment.Horizontal
): TextAlign = when (horizontalAlignment) {
    Alignment.Start -> TextAlign.Start
    Alignment.CenterHorizontally -> TextAlign.Center
    Alignment.End -> TextAlign.End
    else -> TextAlign.Unspecified
}
