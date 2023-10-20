@file:Suppress("MemberVisibilityCanBePrivate")

package com.processout.sdk.ui.core.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.theme.ProcessOutTheme

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
}

@Preview(showBackground = true)
@Composable
internal fun POTextPreview() {
    POText(text = "ProcessOut Payment")
}
