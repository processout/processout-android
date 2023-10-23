@file:Suppress("MemberVisibilityCanBePrivate")

package com.processout.sdk.ui.core.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.style.POTextStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.core.theme.toTextStyle

/** @suppress */
@ProcessOutInternalApi
object POText {

    @Composable
    operator fun invoke(
        text: String,
        modifier: Modifier = Modifier,
        style: Style = body
    ) {
        Text(
            text = text,
            modifier = modifier,
            color = style.color,
            style = style.textStyle
        )
    }

    @Immutable
    data class Style(
        val color: Color,
        val textStyle: TextStyle
    )

    val body: Style
        @Composable get() = Style(
            color = ProcessOutTheme.colors.text.primary,
            textStyle = ProcessOutTheme.typography.fixed.body
        )

    val bodyCompact: Style
        @Composable get() = Style(
            color = ProcessOutTheme.colors.text.primary,
            textStyle = ProcessOutTheme.typography.fixed.bodyCompact
        )

    val label: Style
        @Composable get() = Style(
            color = ProcessOutTheme.colors.text.primary,
            textStyle = ProcessOutTheme.typography.fixed.label
        )

    val labelHeading: Style
        @Composable get() = Style(
            color = ProcessOutTheme.colors.text.primary,
            textStyle = ProcessOutTheme.typography.fixed.labelHeading
        )

    val button: Style
        @Composable get() = Style(
            color = ProcessOutTheme.colors.text.primary,
            textStyle = ProcessOutTheme.typography.fixed.button
        )

    val caption: Style
        @Composable get() = Style(
            color = ProcessOutTheme.colors.text.primary,
            textStyle = ProcessOutTheme.typography.fixed.caption
        )

    val title: Style
        @Composable get() = Style(
            color = ProcessOutTheme.colors.text.primary,
            textStyle = ProcessOutTheme.typography.medium.title
        )

    val subtitle: Style
        @Composable get() = Style(
            color = ProcessOutTheme.colors.text.primary,
            textStyle = ProcessOutTheme.typography.medium.subtitle
        )

    @Composable
    fun custom(style: POTextStyle) = Style(
        color = colorResource(id = style.colorResId),
        textStyle = style.type.toTextStyle()
    )
}

@Preview(showBackground = true)
@Composable
internal fun POTextPreview() {
    POText(text = "ProcessOut Payment")
}
