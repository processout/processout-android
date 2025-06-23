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
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing

/** @suppress */
@ProcessOutInternalApi
object POField {

    @Immutable
    data class Style(
        val normal: StateStyle,
        val error: StateStyle,
        val focused: StateStyle
    )

    @Immutable
    data class StateStyle(
        val text: POText.Style,
        val labelTextColor: Color,
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
                    text = POText.body2,
                    labelTextColor = colors.text.primary,
                    placeholderTextColor = colors.text.muted,
                    backgroundColor = colors.input.backgroundDefault,
                    controlsTintColor = colors.text.primary,
                    dropdownRippleColor = colors.text.muted,
                    shape = shapes.roundedCornersSmall,
                    border = POBorderStroke(width = 1.dp, color = colors.input.borderDefault)
                ),
                error = StateStyle(
                    text = POText.body2,
                    labelTextColor = colors.text.error,
                    placeholderTextColor = colors.text.muted,
                    backgroundColor = colors.input.backgroundDefault,
                    controlsTintColor = colors.text.primary,
                    dropdownRippleColor = colors.text.muted,
                    shape = shapes.roundedCornersSmall,
                    border = POBorderStroke(width = 1.dp, color = colors.input.borderError)
                ),
                focused = StateStyle(
                    text = POText.body2,
                    labelTextColor = colors.text.primary,
                    placeholderTextColor = colors.text.muted,
                    backgroundColor = colors.input.backgroundDefault,
                    controlsTintColor = colors.text.primary,
                    dropdownRippleColor = colors.text.muted,
                    shape = shapes.roundedCornersSmall,
                    border = POBorderStroke(width = 1.dp, color = colors.input.borderFocused)
                )
            )
        }

    val default2: Style
        @Composable get() = with(ProcessOutTheme) {
            Style(
                normal = StateStyle(
                    text = POText.Style(
                        color = colors.text.primary,
                        textStyle = typography.s15(FontWeight.Medium)
                    ),
                    labelTextColor = colors.text.placeholder,
                    placeholderTextColor = colors.text.placeholder,
                    backgroundColor = colors.input.backgroundDefault,
                    controlsTintColor = colors.text.primary,
                    dropdownRippleColor = colors.text.muted,
                    shape = shapes.fieldRoundedCorners,
                    border = POBorderStroke(width = 1.5.dp, color = colors.input.borderDefault2)
                ),
                error = StateStyle(
                    text = POText.Style(
                        color = colors.text.primary,
                        textStyle = typography.s15(FontWeight.Medium)
                    ),
                    labelTextColor = colors.text.placeholder,
                    placeholderTextColor = colors.text.placeholder,
                    backgroundColor = colors.input.backgroundDefault,
                    controlsTintColor = colors.text.primary,
                    dropdownRippleColor = colors.text.muted,
                    shape = shapes.fieldRoundedCorners,
                    border = POBorderStroke(width = 1.5.dp, color = colors.input.borderError)
                ),
                focused = StateStyle(
                    text = POText.Style(
                        color = colors.text.primary,
                        textStyle = typography.s15(FontWeight.Medium)
                    ),
                    labelTextColor = colors.text.placeholder,
                    placeholderTextColor = colors.text.placeholder,
                    backgroundColor = colors.input.backgroundDefault,
                    controlsTintColor = colors.text.primary,
                    dropdownRippleColor = colors.text.muted,
                    shape = shapes.fieldRoundedCorners,
                    border = POBorderStroke(width = 1.5.dp, color = colors.text.primary)
                )
            )
        }

    @Composable
    fun custom(style: POFieldStyle): Style {
        val normal = style.normal.toStateStyle()
        var customStyle = Style(
            normal = normal,
            error = style.error.toStateStyle(),
            focused = style.focused?.toStateStyle() ?: normal
        )
        if (customStyle.normal.labelTextColor == Color.Unspecified) {
            customStyle = customStyle.copy(
                normal = customStyle.normal.copy(
                    labelTextColor = default.normal.labelTextColor
                )
            )
        }
        if (customStyle.error.labelTextColor == Color.Unspecified) {
            customStyle = customStyle.copy(
                error = customStyle.error.copy(
                    labelTextColor = default.error.labelTextColor
                )
            )
        }
        if (customStyle.focused.labelTextColor == Color.Unspecified) {
            customStyle = customStyle.copy(
                focused = customStyle.focused.copy(
                    labelTextColor = default.focused.labelTextColor
                )
            )
        }
        return customStyle
    }

    @Composable
    private fun POFieldStateStyle.toStateStyle() = StateStyle(
        text = POText.custom(style = text),
        labelTextColor = if (labelTextColorResId != 0)
            colorResource(id = labelTextColorResId) else Color.Unspecified,
        placeholderTextColor = colorResource(id = placeholderTextColorResId),
        backgroundColor = colorResource(id = backgroundColorResId),
        controlsTintColor = colorResource(id = controlsTintColorResId),
        dropdownRippleColor = dropdownRippleColorResId?.let { colorResource(id = it) }
            ?: colors.text.muted,
        shape = RoundedCornerShape(size = border.radiusDp.dp),
        border = POBorderStroke(
            width = border.widthDp.dp,
            color = colorResource(id = border.colorResId)
        )
    )

    val contentPadding: PaddingValues
        @Composable get() = PaddingValues(
            horizontal = spacing.large,
            vertical = spacing.medium
        )

    val contentPadding2: PaddingValues
        @Composable get() = PaddingValues(
            horizontal = spacing.space12,
            vertical = spacing.space6
        )

    internal fun Style.stateStyle(
        isError: Boolean,
        isFocused: Boolean
    ): StateStyle =
        if (isError) {
            error
        } else if (isFocused) {
            focused
        } else {
            normal
        }

    @Composable
    internal fun textStyle(
        style: POText.Style,
        forceTextDirectionLtr: Boolean
    ): TextStyle {
        val textStyle = with(style) { textStyle.copy(color = color) }
        if (forceTextDirectionLtr && LocalLayoutDirection.current == LayoutDirection.Rtl) {
            return textStyle.copy(
                textDirection = TextDirection.Ltr,
                textAlign = TextAlign.Right
            )
        }
        return textStyle
    }

    internal fun textSelectionColors(color: Color) =
        TextSelectionColors(
            handleColor = color,
            backgroundColor = color.copy(alpha = 0.4f)
        )

    @Composable
    internal fun ContainerBox(
        style: StateStyle,
        enabled: Boolean,
        isDropdown: Boolean
    ) {
        Box(
            Modifier
                .border(
                    width = style.border.width,
                    color = style.border.color,
                    shape = style.shape
                )
                .background(
                    color = style.backgroundColor,
                    shape = style.shape
                )
                .clip(style.shape)
                .conditional(enabled && isDropdown) {
                    clickable(
                        onClick = {},
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = style.dropdownRippleColor)
                    )
                }
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
