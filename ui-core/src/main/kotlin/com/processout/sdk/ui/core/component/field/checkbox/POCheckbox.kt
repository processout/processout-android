package com.processout.sdk.ui.core.component.field.checkbox

import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.field.checkbox.POCheckbox.CheckboxScale
import com.processout.sdk.ui.core.component.field.checkbox.POCheckbox.CheckboxSize
import com.processout.sdk.ui.core.component.field.checkbox.POCheckbox.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.typography

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    style: POCheckbox.Style = POCheckbox.default,
    enabled: Boolean = true,
    isError: Boolean = false
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier
            .scale(CheckboxScale)
            .requiredWidth(CheckboxSize)
            .requiredHeight(dimensions.formComponentMinHeight),
        enabled = enabled,
        colors = colors(
            style = style,
            enabled = enabled,
            isError = isError
        )
    )
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
        val text: POText.Style
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
                text = POText.body2
            ),
            selected = StateStyle(
                checkmark = CheckmarkStyle(
                    color = colors.surface.default,
                    borderColor = colors.button.primaryBackgroundDefault,
                    backgroundColor = colors.button.primaryBackgroundDefault
                ),
                text = POText.body2
            ),
            error = StateStyle(
                checkmark = CheckmarkStyle(
                    color = colors.input.borderError,
                    borderColor = colors.input.borderError,
                    backgroundColor = colors.surface.default
                ),
                text = POText.body2
            ),
            disabled = StateStyle(
                checkmark = CheckmarkStyle(
                    color = colors.input.borderDisabled,
                    borderColor = colors.input.borderDisabled,
                    backgroundColor = colors.input.backgroundDisabled
                ),
                text = POText.Style(
                    color = colors.text.disabled,
                    textStyle = typography.body2
                )
            )
        )

    private val MaterialCheckboxSize = 20.dp
    internal val CheckboxSize = 22.dp
    internal val CheckboxScale = CheckboxSize.value / MaterialCheckboxSize.value

    @Composable
    internal fun colors(
        style: Style,
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
            with(style.error.checkmark) {
                uncheckedCheckmarkColor = this.color
                uncheckedBorderColor = this.borderColor
                uncheckedBoxColor = this.backgroundColor
                checkedCheckmarkColor = this.color
                checkedBorderColor = this.borderColor
                checkedBoxColor = this.backgroundColor
            }
        } else {
            with(style.normal.checkmark) {
                uncheckedCheckmarkColor = if (enabled) this.color else style.disabled.checkmark.color
                uncheckedBorderColor = this.borderColor
                uncheckedBoxColor = this.backgroundColor
            }
            with(style.selected.checkmark) {
                checkedCheckmarkColor = if (enabled) this.color else style.disabled.checkmark.color
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
            disabledUncheckedBorderColor = style.disabled.checkmark.borderColor,
            disabledUncheckedBoxColor = style.disabled.checkmark.backgroundColor,
            disabledBorderColor = style.disabled.checkmark.borderColor,
            disabledCheckedBoxColor = style.disabled.checkmark.backgroundColor,
            disabledIndeterminateBorderColor = style.disabled.checkmark.borderColor,
            disabledIndeterminateBoxColor = style.disabled.checkmark.backgroundColor
        )
    }
}
