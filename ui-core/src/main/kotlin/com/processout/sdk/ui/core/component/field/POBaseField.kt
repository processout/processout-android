@file:Suppress("MemberVisibilityCanBePrivate")

package com.processout.sdk.ui.core.component.field

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.style.input.POInputStateStyle
import com.processout.sdk.ui.core.style.input.POInputStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme

/** @suppress */
@ProcessOutInternalApi
object POBaseField {

    @Composable
    operator fun invoke(
        title: String,
        description: String = String(),
        style: Style = default,
        isError: Boolean = false,
        content: @Composable () -> Unit
    ) {
        Column {
            POText(
                text = title,
                modifier = Modifier.padding(bottom = ProcessOutTheme.spacing.small),
                color = if (isError) style.error.title.color else style.normal.title.color,
                style = if (isError) style.error.title.textStyle else style.normal.title.textStyle
            )
            content()
            POText(
                text = description,
                modifier = Modifier.padding(top = ProcessOutTheme.spacing.small),
                color = if (isError) style.error.description.color else style.normal.description.color,
                style = if (isError) style.error.description.textStyle else style.normal.description.textStyle
            )
        }
    }

    @Immutable
    data class Style(
        val normal: StateStyle,
        val error: StateStyle
    )

    @Immutable
    data class StateStyle(
        val title: POText.Style,
        val description: POText.Style
    )

    val default: Style
        @Composable get() = with(ProcessOutTheme) {
            Style(
                normal = StateStyle(
                    title = POText.Style(
                        color = colors.text.secondary,
                        textStyle = typography.fixed.labelHeading
                    ),
                    description = POText.Style(
                        color = colors.text.secondary,
                        textStyle = typography.fixed.label
                    )
                ),
                error = StateStyle(
                    title = POText.Style(
                        color = colors.text.secondary,
                        textStyle = typography.fixed.labelHeading
                    ),
                    description = POText.Style(
                        color = colors.text.error,
                        textStyle = typography.fixed.label
                    )
                )
            )
        }

    @Composable
    fun custom(style: POInputStyle) = Style(
        normal = style.normal.toStateStyle(),
        error = style.error.toStateStyle()
    )

    @Composable
    private fun POInputStateStyle.toStateStyle() = StateStyle(
        title = POText.custom(style = title),
        description = POText.custom(style = description)
    )
}
