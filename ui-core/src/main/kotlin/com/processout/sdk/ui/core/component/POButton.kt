package com.processout.sdk.ui.core.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POButton.border
import com.processout.sdk.ui.core.component.POButton.colors
import com.processout.sdk.ui.core.component.POButton.contentPadding
import com.processout.sdk.ui.core.component.POButton.elevation
import com.processout.sdk.ui.core.extension.conditional
import com.processout.sdk.ui.core.style.POButtonDefaults
import com.processout.sdk.ui.core.style.POButtonStateStyle
import com.processout.sdk.ui.core.style.POButtonStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing

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
    leadingContent: @Composable RowScope.() -> Unit = {},
    @DrawableRes iconResId: Int? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val pressed by interactionSource.collectIsPressedAsState()
    val colors = colors(style = style, enabled = enabled, loading = loading, pressed = pressed)
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
        Button(
            onClick = onClick,
            modifier = modifier.requiredHeightIn(
                min = ProcessOutTheme.dimensions.interactiveComponentMinSize
            ),
            enabled = enabled && !loading,
            colors = colors,
            shape = if (enabled) style.normal.shape else style.disabled.shape,
            border = border(style = style, enabled = enabled, pressed = pressed),
            elevation = elevation(style = style, enabled = enabled, loading = loading),
            contentPadding = contentPadding(style = style, enabled = enabled),
            interactionSource = interactionSource
        ) {
            if (loading) {
                Box(contentAlignment = Alignment.Center) {
                    POCircularProgressIndicator.Small(color = style.progressIndicatorColor)
                    // This empty POText ensures that button height matches with provided text style while loading.
                    POText(
                        text = String(),
                        style = style.normal.text.textStyle
                    )
                }
            } else {
                leadingContent()
                iconResId?.let {
                    val iconColor = if (enabled) colors.contentColor else colors.disabledContentColor
                    Image(
                        painter = painterResource(it),
                        contentDescription = null,
                        modifier = Modifier
                            .conditional(text.isNotBlank()) {
                                padding(end = spacing.small)
                            }
                            .requiredSize(20.dp),
                        colorFilter = ColorFilter.tint(color = iconColor)
                    )
                }
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
                        color = colors.text.inverse,
                        textStyle = typography.button
                    ),
                    shape = shapes.roundedCornersSmall,
                    border = POBorderStroke(width = 0.dp, color = Color.Transparent),
                    backgroundColor = colors.button.primaryBackgroundDefault,
                    elevation = 0.dp
                ),
                disabled = StateStyle(
                    text = POText.Style(
                        color = colors.text.disabled,
                        textStyle = typography.button
                    ),
                    shape = shapes.roundedCornersSmall,
                    border = POBorderStroke(width = 0.dp, color = Color.Transparent),
                    backgroundColor = colors.button.primaryBackgroundDisabled,
                    elevation = 0.dp
                ),
                highlighted = HighlightedStyle(
                    textColor = colors.text.inverse,
                    borderColor = Color.Transparent,
                    backgroundColor = colors.button.primaryBackgroundPressed
                ),
                progressIndicatorColor = colors.text.inverse
            )
        }

    val secondary: Style
        @Composable get() = with(ProcessOutTheme) {
            Style(
                normal = StateStyle(
                    text = POText.Style(
                        color = colors.text.primary,
                        textStyle = typography.button
                    ),
                    shape = shapes.roundedCornersSmall,
                    border = POBorderStroke(width = 1.dp, color = colors.button.secondaryBorderDefault),
                    backgroundColor = colors.button.secondaryBackgroundDefault,
                    elevation = 0.dp
                ),
                disabled = StateStyle(
                    text = POText.Style(
                        color = colors.text.disabled,
                        textStyle = typography.button
                    ),
                    shape = shapes.roundedCornersSmall,
                    border = POBorderStroke(width = 1.dp, color = colors.button.secondaryBorderDisabled),
                    backgroundColor = colors.button.secondaryBackgroundDisabled,
                    elevation = 0.dp
                ),
                highlighted = HighlightedStyle(
                    textColor = colors.text.primary,
                    borderColor = colors.button.secondaryBorderPressed,
                    backgroundColor = colors.button.secondaryBackgroundPressed
                ),
                progressIndicatorColor = colors.text.primary
            )
        }

    val tertiary: Style
        @Composable get() = with(ProcessOutTheme) {
            Style(
                normal = StateStyle(
                    text = POText.Style(
                        color = colors.text.primary,
                        textStyle = typography.body1
                    ),
                    shape = shapes.roundedCornersSmall,
                    border = POBorderStroke(width = 0.dp, color = Color.Transparent),
                    backgroundColor = Color.Transparent,
                    elevation = 0.dp,
                    paddingHorizontal = spacing.large
                ),
                disabled = StateStyle(
                    text = POText.Style(
                        color = colors.text.disabled,
                        textStyle = typography.body1
                    ),
                    shape = shapes.roundedCornersSmall,
                    border = POBorderStroke(width = 0.dp, color = Color.Transparent),
                    backgroundColor = Color.Transparent,
                    elevation = 0.dp,
                    paddingHorizontal = spacing.large
                ),
                highlighted = HighlightedStyle(
                    textColor = colors.text.primary,
                    borderColor = Color.Transparent,
                    backgroundColor = colors.button.tertiaryBackgroundPressed
                ),
                progressIndicatorColor = colors.text.primary
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
        style: Style,
        enabled: Boolean,
        loading: Boolean,
        pressed: Boolean
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
        style: Style,
        enabled: Boolean,
        pressed: Boolean
    ): BorderStroke {
        val normalBorderColor = if (pressed) style.highlighted.borderColor else style.normal.border.color
        return if (enabled) style.normal.border.solid(color = normalBorderColor)
        else style.disabled.border.solid()
    }

    @Composable
    internal fun elevation(
        style: Style,
        enabled: Boolean,
        loading: Boolean
    ): ButtonElevation = ButtonDefaults.buttonElevation(
        defaultElevation = style.normal.elevation,
        pressedElevation = style.normal.elevation,
        focusedElevation = style.normal.elevation,
        hoveredElevation = style.normal.elevation,
        disabledElevation = if (enabled && loading)
            style.normal.elevation else style.disabled.elevation
    )

    internal fun contentPadding(
        style: Style,
        enabled: Boolean
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
