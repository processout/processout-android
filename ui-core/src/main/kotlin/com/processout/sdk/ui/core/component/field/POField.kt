package com.processout.sdk.ui.core.component.field

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POBorderStroke
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.style.POFieldStateStyle
import com.processout.sdk.ui.core.style.POFieldStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme

/** @suppress */
@ProcessOutInternalApi
object POField {

    @Immutable
    data class Style(
        val normal: StateStyle,
        val error: StateStyle
    )

    @Immutable
    data class StateStyle(
        val text: POText.Style,
        val placeholderTextColor: Color,
        val backgroundColor: Color,
        val controlsTintColor: Color,
        val shape: Shape,
        val border: POBorderStroke
    )

    val default: Style
        @Composable get() = with(ProcessOutTheme) {
            Style(
                normal = StateStyle(
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
                error = StateStyle(
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
    fun custom(style: POFieldStyle) = Style(
        normal = style.normal.toStateStyle(),
        error = style.error.toStateStyle()
    )

    @Composable
    private fun POFieldStateStyle.toStateStyle() = StateStyle(
        text = POText.custom(style = text),
        placeholderTextColor = colorResource(id = placeholderTextColorResId),
        backgroundColor = colorResource(id = backgroundColorResId),
        controlsTintColor = colorResource(id = controlsTintColorResId),
        shape = RoundedCornerShape(size = border.radiusDp.dp),
        border = POBorderStroke(
            width = border.widthDp.dp,
            color = colorResource(id = border.colorResId)
        )
    )

    val contentPadding: PaddingValues
        @Composable get() = PaddingValues(
            horizontal = ProcessOutTheme.spacing.medium,
            vertical = ProcessOutTheme.spacing.small
        )

    @Composable
    internal fun textStyle(
        isError: Boolean,
        forceTextDirectionLtr: Boolean,
        style: Style
    ): TextStyle {
        val textStyle = when (isError) {
            true -> with(style.error.text) { textStyle.copy(color = color) }
            false -> with(style.normal.text) { textStyle.copy(color = color) }
        }
        if (forceTextDirectionLtr && LocalLayoutDirection.current == LayoutDirection.Rtl) {
            return textStyle.copy(
                textDirection = TextDirection.Ltr,
                textAlign = TextAlign.Right
            )
        }
        return textStyle
    }

    internal fun textSelectionColors(
        isError: Boolean,
        style: Style
    ): TextSelectionColors {
        val color = if (isError) style.error.controlsTintColor else style.normal.controlsTintColor
        return TextSelectionColors(
            handleColor = color,
            backgroundColor = color.copy(alpha = 0.4f)
        )
    }

    internal fun cursorBrush(
        isError: Boolean,
        style: Style
    ) = SolidColor(value = if (isError) style.error.controlsTintColor else style.normal.controlsTintColor)

    @Composable
    internal fun ContainerBox(
        isError: Boolean,
        style: Style
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
    internal fun Placeholder(
        text: String,
        isError: Boolean,
        style: Style
    ) {
        POText(
            text = text,
            color = if (isError) style.error.placeholderTextColor else style.normal.placeholderTextColor,
            style = if (isError) style.error.text.textStyle else style.normal.text.textStyle
        )
    }
}
