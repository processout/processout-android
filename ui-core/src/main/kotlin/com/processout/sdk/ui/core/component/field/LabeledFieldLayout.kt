package com.processout.sdk.ui.core.component.field

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.processout.sdk.ui.core.component.POExpandableText
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.theme.ProcessOutTheme

@Composable
internal fun LabeledFieldLayout(
    title: String,
    description: String?,
    style: POFieldLabels.Style = POFieldLabels.default,
    content: @Composable () -> Unit
) {
    Column {
        POText(
            text = title,
            modifier = Modifier.padding(bottom = ProcessOutTheme.spacing.small),
            color = style.title.color,
            style = style.title.textStyle
        )
        content()
        POExpandableText(
            text = description,
            style = style.description,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = ProcessOutTheme.spacing.small)
        )
    }
}
