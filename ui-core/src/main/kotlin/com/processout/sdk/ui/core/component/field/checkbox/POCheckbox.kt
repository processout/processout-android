package com.processout.sdk.ui.core.component.field.checkbox

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.POText.measuredPaddingTop
import com.processout.sdk.ui.core.component.field.checkbox.POCheckbox.MaterialCheckboxSize
import com.processout.sdk.ui.core.component.field.checkbox.POCheckbox.colors
import com.processout.sdk.ui.core.component.field.checkbox.POCheckbox.stateStyle
import com.processout.sdk.ui.core.style.POCheckboxStateStyle
import com.processout.sdk.ui.core.style.POCheckboxStyle
import com.processout.sdk.ui.core.style.POCheckmarkStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.core.theme.ProcessOutTheme.typography

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POCheckbox(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    minHeight: Dp = dimensions.formComponentMinHeight,
    checkboxSize: Dp = MaterialCheckboxSize,
    rowShape: CornerBasedShape = shapes.roundedCorners4,
    style: POCheckbox.Style = POCheckbox.default,
    enabled: Boolean = true,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val stateStyle = style.stateStyle(
        checked = checked,
        enabled = enabled,
        isError = isError
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .requiredHeightIn(min = minHeight)
            .clip(shape = rowShape)
            .clickable(
                onClick = {
                    if (enabled) {
                        onCheckedChange(!checked)
                    }
                },
                interactionSource = interactionSource,
                indication = stateStyle.rippleColor?.let { ripple(color = it) }
            )
    ) {
        val checkboxScale = checkboxSize.value / MaterialCheckboxSize.value
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier
                .scale(checkboxScale)
                .requiredWidth(checkboxSize)
                .requiredHeight(minHeight),
            enabled = enabled,
            colors = style.colors(
                enabled = enabled,
                isError = isError
            )
        )
        POText(
            text = text,
            modifier = Modifier.padding(
                start = spacing.space10,
                top = measuredPaddingTop(
                    textStyle = stateStyle.text.textStyle,
                    componentHeight = minHeight
                ),
                bottom = spacing.space10
            ),
            color = stateStyle.text.color,
            style = stateStyle.text.textStyle
        )
    }
}

/** @suppress */
@ProcessOutInternalApi
object POCheckbox {

    @Immutable
    data class Style(
        val normal: StateStyle,
        val selected: StateStyle,
        val error: StateStyle,
        val disabled: StateStyle
    )

    @Immutable
    data class StateStyle(
        val checkmark: CheckmarkStyle,
        val text: POText.Style,
        val rippleColor: Color?
    )

    @Immutable
    data class CheckmarkStyle(
        val color: Color,
        val borderColor: Color,
        val backgroundColor: Color
    )

    val default: Style
        @Composable get() = Style(
            normal = StateStyle(
                checkmark = CheckmarkStyle(
                    color = colors.surface.default,
                    borderColor = colors.input.borderDefault,
                    backgroundColor = colors.surface.default
                ),
                text = POText.label1,
                rippleColor = colors.surface.darkoutRipple
            ),
            selected = StateStyle(
                checkmark = CheckmarkStyle(
                    color = colors.surface.default,
                    borderColor = colors.button.primaryBackgroundDefault,
                    backgroundColor = colors.button.primaryBackgroundDefault
                ),
                text = POText.label1,
                rippleColor = colors.surface.darkoutRipple
            ),
            error = StateStyle(
                checkmark = CheckmarkStyle(
                    color = colors.input.borderError,
                    borderColor = colors.input.borderError,
                    backgroundColor = colors.surface.default
                ),
                text = POText.label1,
                rippleColor = colors.surface.darkoutRipple
            ),
            disabled = StateStyle(
                checkmark = CheckmarkStyle(
                    color = colors.input.borderDisabled,
                    borderColor = colors.input.borderDisabled,
                    backgroundColor = colors.input.backgroundDisabled
                ),
                text = POText.Style(
                    color = colors.text.disabled,
                    textStyle = typography.label1
                ),
                rippleColor = null
            )
        )

    val default2: Style
        @Composable get() = Style(
            normal = StateStyle(
                checkmark = CheckmarkStyle(
                    color = colors.checkRadio.iconDefault,
                    borderColor = colors.checkRadio.borderDefault,
                    backgroundColor = colors.checkRadio.surfaceDefault
                ),
                text = POText.Style(
                    color = colors.text.secondary,
                    textStyle = typography.s15(FontWeight.Medium)
                ),
                rippleColor = colors.surface.darkoutRipple
            ),
            selected = StateStyle(
                checkmark = CheckmarkStyle(
                    color = colors.checkRadio.iconActive,
                    borderColor = colors.checkRadio.borderActive,
                    backgroundColor = colors.checkRadio.surfaceActive
                ),
                text = POText.Style(
                    color = colors.text.secondary,
                    textStyle = typography.s15(FontWeight.Medium)
                ),
                rippleColor = colors.surface.darkoutRipple
            ),
            error = StateStyle(
                checkmark = CheckmarkStyle(
                    color = colors.checkRadio.iconError,
                    borderColor = colors.checkRadio.borderError,
                    backgroundColor = colors.checkRadio.surfaceError
                ),
                text = POText.Style(
                    color = colors.text.secondary,
                    textStyle = typography.s15(FontWeight.Medium)
                ),
                rippleColor = colors.surface.darkoutRipple
            ),
            disabled = StateStyle(
                checkmark = CheckmarkStyle(
                    color = colors.checkRadio.iconDisabled,
                    borderColor = colors.checkRadio.borderDisabled,
                    backgroundColor = colors.checkRadio.surfaceDisabled
                ),
                text = POText.Style(
                    color = colors.text.disabled,
                    textStyle = typography.s15(FontWeight.Medium)
                ),
                rippleColor = null
            )
        )

    @Composable
    fun custom(style: POCheckboxStyle) = Style(
        normal = style.normal.toStateStyle(),
        selected = style.selected.toStateStyle(),
        error = style.error.toStateStyle(),
        disabled = style.disabled.toStateStyle()
    )

    @Composable
    private fun POCheckboxStateStyle.toStateStyle() = StateStyle(
        checkmark = checkmark.toCheckmarkStyle(),
        text = POText.custom(style = text),
        rippleColor = rippleColorResId?.let { colorResource(id = it) }
    )

    @Composable
    private fun POCheckmarkStyle.toCheckmarkStyle() = CheckmarkStyle(
        color = colorResource(id = colorResId),
        borderColor = colorResource(id = borderColorResId),
        backgroundColor = colorResource(id = backgroundColorResId)
    )

    internal val MaterialCheckboxSize = 20.dp

    internal fun Style.colors(
        enabled: Boolean,
        isError: Boolean
    ): CheckboxColors {
        val uncheckedCheckmarkColor: Color
        val uncheckedBorderColor: Color
        val uncheckedBoxColor: Color
        val checkedCheckmarkColor: Color
        val checkedBorderColor: Color
        val checkedBoxColor: Color
        if (isError) {
            with(error.checkmark) {
                uncheckedCheckmarkColor = this.color
                uncheckedBorderColor = this.borderColor
                uncheckedBoxColor = this.backgroundColor
                checkedCheckmarkColor = if (enabled) this.color else disabled.checkmark.color
                checkedBorderColor = this.borderColor
                checkedBoxColor = this.backgroundColor
            }
        } else {
            with(normal.checkmark) {
                uncheckedCheckmarkColor = if (enabled) this.color else disabled.checkmark.color
                uncheckedBorderColor = this.borderColor
                uncheckedBoxColor = this.backgroundColor
            }
            with(selected.checkmark) {
                checkedCheckmarkColor = if (enabled) this.color else disabled.checkmark.color
                checkedBorderColor = this.borderColor
                checkedBoxColor = this.backgroundColor
            }
        }
        return CheckboxColors(
            uncheckedCheckmarkColor = uncheckedCheckmarkColor,
            uncheckedBorderColor = uncheckedBorderColor,
            uncheckedBoxColor = uncheckedBoxColor,
            checkedCheckmarkColor = checkedCheckmarkColor,
            checkedBorderColor = checkedBorderColor,
            checkedBoxColor = checkedBoxColor,
            disabledUncheckedBorderColor = disabled.checkmark.borderColor,
            disabledUncheckedBoxColor = disabled.checkmark.backgroundColor,
            disabledBorderColor = disabled.checkmark.borderColor,
            disabledCheckedBoxColor = disabled.checkmark.backgroundColor,
            disabledIndeterminateBorderColor = disabled.checkmark.borderColor,
            disabledIndeterminateBoxColor = disabled.checkmark.backgroundColor
        )
    }

    internal fun Style.stateStyle(
        checked: Boolean,
        enabled: Boolean,
        isError: Boolean
    ): StateStyle =
        if (!enabled) disabled
        else if (isError) error
        else if (checked) selected
        else normal
}
