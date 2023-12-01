@file:Suppress("MemberVisibilityCanBePrivate")

package com.processout.sdk.ui.core.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.style.POButtonDefaults
import com.processout.sdk.ui.core.style.POButtonStateStyle
import com.processout.sdk.ui.core.style.POButtonStyle
import com.processout.sdk.ui.core.theme.NoRippleTheme
import com.processout.sdk.ui.core.theme.ProcessOutTheme

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: POButton.Style = POButton.primary,
    enabled: Boolean = true,
    loading: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val pressed by interactionSource.collectIsPressedAsState()
    CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
        Button(
            onClick = onClick,
            modifier = modifier.defaultMinSize(minHeight = ProcessOutTheme.dimensions.formComponentHeight),
            enabled = enabled && !loading,
            colors = POButton.colors(enabled = enabled, loading = loading, pressed = pressed, style = style),
            shape = if (enabled) style.normal.shape else style.disabled.shape,
            border = POButton.border(enabled = enabled, pressed = pressed, style = style),
            elevation = POButton.elevation(enabled = enabled, loading = loading, style = style),
            contentPadding = POButton.contentPadding(enabled = enabled, style = style),
            interactionSource = interactionSource
        ) {
            if (enabled && loading) {
                POCircularProgressIndicator.Small(color = style.progressIndicatorColor)
            } else {
                POText(
                    text = text,
                    style = if (enabled) style.normal.text.textStyle else style.disabled.text.textStyle,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    }
}

/** @suppress */
@ProcessOutInternalApi
object POButton {

    @Immutable
    data class Style(
        val normal: StateStyle,
        val disabled: StateStyle,
        val highlighted: HighlightedStyle,
        val progressIndicatorColor: Color
    )

    @Immutable
    data class StateStyle(
        val text: POText.Style,
        val shape: Shape,
        val border: POBorderStroke,
        val backgroundColor: Color,
        val elevation: Dp,
        val paddingHorizontal: Dp = POButtonDefaults.PADDING_HORIZONTAL_DP.dp,
        val paddingVertical: Dp = POButtonDefaults.PADDING_VERTICAL_DP.dp
    )

    @Immutable
    data class HighlightedStyle(
        val textColor: Color,
        val borderColor: Color,
        val backgroundColor: Color
    )

    val primary: Style
        @Composable get() = with(ProcessOutTheme) {
            Style(
                normal = StateStyle(
                    text = POText.Style(
                        color = colors.text.onColor,
                        textStyle = typography.fixed.button
                    ),
                    shape = shapes.roundedCornersSmall,
                    border = POBorderStroke(width = 0.dp, color = Color.Transparent),
                    backgroundColor = colors.action.primaryDefault,
                    elevation = 2.dp
                ),
                disabled = StateStyle(
                    text = POText.Style(
                        color = colors.text.disabled,
                        textStyle = typography.fixed.button
                    ),
                    shape = shapes.roundedCornersSmall,
                    border = POBorderStroke(width = 0.dp, color = Color.Transparent),
                    backgroundColor = colors.action.primaryDisabled,
                    elevation = 0.dp
                ),
                highlighted = HighlightedStyle(
                    textColor = colors.text.onColor,
                    borderColor = Color.Transparent,
                    backgroundColor = colors.action.primaryPressed
                ),
                progressIndicatorColor = colors.text.onColor
            )
        }

    val secondary: Style
        @Composable get() = with(ProcessOutTheme) {
            Style(
                normal = StateStyle(
                    text = POText.Style(
                        color = colors.text.secondary,
                        textStyle = typography.fixed.button
                    ),
                    shape = shapes.roundedCornersSmall,
                    border = POBorderStroke(width = 1.dp, color = colors.border.default),
                    backgroundColor = colors.action.secondaryDefault,
                    elevation = 0.dp
                ),
                disabled = StateStyle(
                    text = POText.Style(
                        color = colors.text.disabled,
                        textStyle = typography.fixed.button
                    ),
                    shape = shapes.roundedCornersSmall,
                    border = POBorderStroke(width = 1.dp, color = colors.border.disabled),
                    backgroundColor = colors.action.secondaryDefault,
                    elevation = 0.dp
                ),
                highlighted = HighlightedStyle(
                    textColor = colors.text.secondary,
                    borderColor = colors.border.default,
                    backgroundColor = colors.action.secondaryPressed
                ),
                progressIndicatorColor = colors.text.secondary
            )
        }

    @Composable
    fun custom(style: POButtonStyle) = Style(
        normal = style.normal.toStateStyle(),
        disabled = style.disabled.toStateStyle(),
        highlighted = with(style.highlighted) {
            HighlightedStyle(
                textColor = colorResource(id = textColorResId),
                borderColor = colorResource(id = borderColorResId),
                backgroundColor = colorResource(id = backgroundColorResId)
            )
        },
        progressIndicatorColor = colorResource(id = style.progressIndicatorColorResId)
    )

    @Composable
    private fun POButtonStateStyle.toStateStyle() = StateStyle(
        text = POText.custom(style = text),
        shape = RoundedCornerShape(size = border.radiusDp.dp),
        border = POBorderStroke(width = border.widthDp.dp, color = colorResource(id = border.colorResId)),
        backgroundColor = colorResource(id = backgroundColorResId),
        elevation = elevationDp.dp,
        paddingHorizontal = paddingHorizontalDp.dp,
        paddingVertical = paddingVerticalDp.dp
    )

    @Composable
    internal fun colors(
        enabled: Boolean,
        loading: Boolean,
        pressed: Boolean,
        style: Style
    ): ButtonColors {
        val normalTextColor: Color
        val normalBackgroundColor: Color
        if (pressed) with(style.highlighted) {
            normalTextColor = textColor
            normalBackgroundColor = backgroundColor
        } else with(style.normal) {
            normalTextColor = text.color
            normalBackgroundColor = backgroundColor
        }

        val disabledTextColor: Color
        val disabledBackgroundColor: Color
        if (enabled && loading) with(style.normal) {
            disabledTextColor = text.color
            disabledBackgroundColor = backgroundColor
        } else with(style.disabled) {
            disabledTextColor = text.color
            disabledBackgroundColor = backgroundColor
        }

        return ButtonDefaults.buttonColors(
            containerColor = normalBackgroundColor,
            contentColor = normalTextColor,
            disabledContainerColor = disabledBackgroundColor,
            disabledContentColor = disabledTextColor
        )
    }

    internal fun border(
        enabled: Boolean,
        pressed: Boolean,
        style: Style
    ): BorderStroke {
        val normalBorderColor = if (pressed) style.highlighted.borderColor else style.normal.border.color
        return if (enabled) style.normal.border.solid(color = normalBorderColor)
        else style.disabled.border.solid()
    }

    @Composable
    internal fun elevation(
        enabled: Boolean,
        loading: Boolean,
        style: Style
    ): ButtonElevation = ButtonDefaults.buttonElevation(
        defaultElevation = style.normal.elevation,
        pressedElevation = style.normal.elevation,
        focusedElevation = style.normal.elevation,
        hoveredElevation = style.normal.elevation,
        disabledElevation = if (enabled && loading)
            style.normal.elevation else style.disabled.elevation
    )

    internal fun contentPadding(
        enabled: Boolean,
        style: Style
    ): PaddingValues = if (enabled) PaddingValues(
        horizontal = style.normal.paddingHorizontal,
        vertical = style.normal.paddingVertical
    ) else PaddingValues(
        horizontal = style.disabled.paddingHorizontal,
        vertical = style.disabled.paddingVertical
    )
}

@Composable
@Preview(showBackground = true)
private fun POButtonPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        POButton(
            text = "Button",
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
