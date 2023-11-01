package com.processout.sdk.ui.core.component.field

import androidx.compose.runtime.Composable
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.style.input.POInputStateStyle
import com.processout.sdk.ui.core.style.input.POInputStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme

/** @suppress */
@ProcessOutInternalApi
object POFieldLabelsDefaults {

    val default: POFieldLabelsStyle
        @Composable get() = with(ProcessOutTheme) {
            POFieldLabelsStyle(
                normal = POFieldLabelsStateStyle(
                    title = POText.Style(
                        color = colors.text.secondary,
                        textStyle = typography.fixed.labelHeading
                    ),
                    description = POText.Style(
                        color = colors.text.secondary,
                        textStyle = typography.fixed.label
                    )
                ),
                error = POFieldLabelsStateStyle(
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
    fun custom(style: POInputStyle) = POFieldLabelsStyle(
        normal = style.normal.toStateStyle(),
        error = style.error.toStateStyle()
    )

    @Composable
    private fun POInputStateStyle.toStateStyle() = POFieldLabelsStateStyle(
        title = POText.custom(style = title),
        description = POText.custom(style = description)
    )
}
