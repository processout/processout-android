package com.processout.sdk.ui.core.component.field

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POBorderStroke
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.style.input.POInputStateStyle
import com.processout.sdk.ui.core.style.input.POInputStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme

/** @suppress */
@ProcessOutInternalApi
object POFieldDefaults {

    val ContentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)

    fun textStyle(
        isError: Boolean,
        style: POFieldStyle
    ): TextStyle =
        if (isError) with(style.error.text) { textStyle.copy(color = color) }
        else with(style.normal.text) { textStyle.copy(color = color) }

    fun textSelectionColors(
        isError: Boolean,
        style: POFieldStyle
    ): TextSelectionColors {
        val color = if (isError) style.error.controlsTintColor else style.normal.controlsTintColor
        return TextSelectionColors(
            handleColor = color,
            backgroundColor = color.copy(alpha = 0.4f)
        )
    }

    fun cursorBrush(
        isError: Boolean,
        style: POFieldStyle
    ) = SolidColor(value = if (isError) style.error.controlsTintColor else style.normal.controlsTintColor)

    @Composable
    fun ContainerBox(
        isError: Boolean,
        style: POFieldStyle
    ) {
        val shape = if (isError) style.error.shape else style.normal.shape
        Box(
            Modifier
                .border(
                    width = if (isError) style.error.border.width else style.normal.border.width,
                    color = if (isError) style.error.border.color else style.normal.border.color,
                    shape = shape
                )
                .background(
                    color = if (isError) style.error.backgroundColor else style.normal.backgroundColor,
                    shape = shape
                )
        )
    }

    @Composable
    fun Placeholder(
        text: String,
        isError: Boolean,
        style: POFieldStyle
    ) {
        POText(
            text = text,
            color = if (isError) style.error.placeholderTextColor else style.normal.placeholderTextColor,
            style = if (isError) style.error.text.textStyle else style.normal.text.textStyle
        )
    }

    val default: POFieldStyle
        @Composable get() = with(ProcessOutTheme) {
            POFieldStyle(
                normal = POFieldStateStyle(
                    text = POText.Style(
                        color = colors.text.primary,
                        textStyle = typography.fixed.label
                    ),
                    placeholderTextColor = colors.text.muted,
                    backgroundColor = colors.surface.background,
                    controlsTintColor = colors.text.primary,
                    shape = shapes.roundedCornersSmall,
                    border = POBorderStroke(width = 1.dp, color = colors.border.default)
                ),
                error = POFieldStateStyle(
                    text = POText.Style(
                        color = colors.text.primary,
                        textStyle = typography.fixed.label
                    ),
                    placeholderTextColor = colors.text.muted,
                    backgroundColor = colors.surface.background,
                    controlsTintColor = colors.text.error,
                    shape = shapes.roundedCornersSmall,
                    border = POBorderStroke(width = 1.dp, color = colors.text.error)
                )
            )
        }

    @Composable
    fun custom(style: POInputStyle) = POFieldStyle(
        normal = style.normal.toStateStyle(),
        error = style.error.toStateStyle()
    )

    @Composable
    private fun POInputStateStyle.toStateStyle() = with(field) {
        POFieldStateStyle(
            text = POText.custom(style = text),
            placeholderTextColor = colorResource(id = placeholderTextColorResId),
            backgroundColor = colorResource(id = backgroundColorResId),
            controlsTintColor = colorResource(id = controlsTintColorResId),
            shape = RoundedCornerShape(size = border.radiusDp.dp),
            border = POBorderStroke(width = border.widthDp.dp, color = colorResource(id = border.colorResId))
        )
    }
}
