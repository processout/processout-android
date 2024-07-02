package com.processout.sdk.ui.core.component.field

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.ActionId
import com.processout.sdk.ui.core.component.POBorderStroke
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.extension.conditional
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
        val dropdownRippleColor: Color,
        val shape: Shape,
        val border: POBorderStroke
    )

    val default: Style
        @Composable get() = with(ProcessOutTheme) {
            Style(
                normal = StateStyle(
                    text = POText.Style(
                        color = colors.text.primary,
                        textStyle = typography.label2
                    ),
                    placeholderTextColor = colors.text.muted,
                    backgroundColor = colors.input.backgroundDefault,
                    controlsTintColor = colors.text.primary,
                    dropdownRippleColor = colors.text.muted,
                    shape = shapes.roundedCornersSmall,
                    border = POBorderStroke(width = 1.dp, color = colors.input.borderDefault)
                ),
                error = StateStyle(
                    text = POText.Style(
                        color = colors.text.primary,
                        textStyle = typography.label2
                    ),
                    placeholderTextColor = colors.text.muted,
                    backgroundColor = colors.input.backgroundDefault,
                    controlsTintColor = colors.text.error,
                    dropdownRippleColor = colors.text.muted,
                    shape = shapes.roundedCornersSmall,
                    border = POBorderStroke(width = 1.dp, color = colors.input.borderError)
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
        dropdownRippleColor = dropdownRippleColorResId?.let { colorResource(id = it) }
            ?: ProcessOutTheme.colors.text.muted,
        shape = RoundedCornerShape(size = border.radiusDp.dp),
        border = POBorderStroke(
            width = border.widthDp.dp,
            color = colorResource(id = border.colorResId)
        )
    )

    val contentPadding: PaddingValues
        @Composable get() = PaddingValues(
            horizontal = ProcessOutTheme.spacing.large,
            vertical = ProcessOutTheme.spacing.medium
        )

    @Composable
    internal fun textStyle(
        style: Style,
        isError: Boolean,
        forceTextDirectionLtr: Boolean
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
        style: Style,
        isError: Boolean
    ): TextSelectionColors {
        val color = if (isError) style.error.controlsTintColor else style.normal.controlsTintColor
        return TextSelectionColors(
            handleColor = color,
            backgroundColor = color.copy(alpha = 0.4f)
        )
    }

    internal fun cursorBrush(
        style: Style,
        isError: Boolean
    ) = SolidColor(value = if (isError) style.error.controlsTintColor else style.normal.controlsTintColor)

    @Composable
    internal fun ContainerBox(
        style: Style,
        isDropdown: Boolean,
        isError: Boolean
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
                .clip(shape)
                .conditional(isDropdown) {
                    clickable(
                        onClick = {},
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            color = if (isError) style.error.dropdownRippleColor else style.normal.dropdownRippleColor
                        )
                    )
                }
        )
    }

    @Composable
    internal fun Placeholder(
        style: Style,
        text: String,
        isError: Boolean
    ) {
        POText(
            text = text,
            color = if (isError) style.error.placeholderTextColor else style.normal.placeholderTextColor,
            style = if (isError) style.error.text.textStyle else style.normal.text.textStyle
        )
    }

    fun keyboardActions(
        imeAction: ImeAction,
        actionId: String?,
        enabled: Boolean,
        onClick: (ActionId) -> Unit
    ) = when (imeAction) {
        ImeAction.Done -> {
            if (enabled && actionId != null)
                KeyboardActions(onDone = { onClick(actionId) })
            else KeyboardActions(onDone = {})
        }
        else -> KeyboardActions.Default
    }
}
