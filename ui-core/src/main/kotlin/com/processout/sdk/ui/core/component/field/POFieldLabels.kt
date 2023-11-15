package com.processout.sdk.ui.core.component.field

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.style.input.POInputStateStyle
import com.processout.sdk.ui.core.style.input.POInputStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme

/** @suppress */
@ProcessOutInternalApi
object POFieldLabels {

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
