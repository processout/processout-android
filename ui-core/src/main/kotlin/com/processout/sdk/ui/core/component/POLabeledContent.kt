package com.processout.sdk.ui.core.component

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.style.POLabeledContentStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.core.theme.ProcessOutTheme.typography

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POLabeledContent(
    label: String,
    modifier: Modifier = Modifier,
    labelStyle: POText.Style = POText.Style(
        color = colors.text.placeholder,
        textStyle = typography.s12(FontWeight.Medium)
    ),
    trailingContent: @Composable () -> Unit = {},
    trailingContentAlignment: Alignment = Alignment.TopStart,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = spacing.space8)
                .weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(spacing.space6)
        ) {
            POText(
                text = label,
                color = labelStyle.color,
                style = labelStyle.textStyle
            )
            content()
        }
        Box(
            modifier = Modifier.fillMaxHeight(),
            contentAlignment = trailingContentAlignment
        ) {
            trailingContent()
        }
    }
}

/** @suppress */
@ProcessOutInternalApi
object POLabeledContent {

    @Immutable
    data class Style(
        val label: POText.Style,
        val text: POText.Style,
        val copyButton: POButton.Style
    )

    val default: Style
        @Composable get() = Style(
            label = POText.Style(
                color = colors.text.placeholder,
                textStyle = typography.s12(FontWeight.Medium)
            ),
            text = POText.Style(
                color = colors.text.primary,
                textStyle = typography.s15(FontWeight.Medium)
            ),
            copyButton = POCopyButton.default
        )

    @Composable
    fun custom(style: POLabeledContentStyle) = Style(
        label = POText.custom(style = style.label),
        text = POText.custom(style = style.text),
        copyButton = default.copyButton
    )
}
