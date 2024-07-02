package com.processout.sdk.ui.core.component.field.radio

import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.field.radio.PORadioButton.RadioButtonScale
import com.processout.sdk.ui.core.component.field.radio.PORadioButton.RadioButtonSize
import com.processout.sdk.ui.core.component.field.radio.PORadioButton.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme

/** @suppress */
@ProcessOutInternalApi
@Composable
fun PORadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: PORadioButton.Style = PORadioButton.default,
    enabled: Boolean = true,
    isError: Boolean = false
) {
    RadioButton(
        selected = selected,
        onClick = onClick,
        modifier = modifier
            .scale(RadioButtonScale)
            .requiredWidth(RadioButtonSize)
            .requiredHeight(ProcessOutTheme.dimensions.formComponentMinHeight),
        enabled = enabled,
        colors = colors(style = style, isError = isError)
    )
}

/** @suppress */
@ProcessOutInternalApi
object PORadioButton {

    @Immutable
    data class Style(
        val normalColor: Color,
        val selectedColor: Color,
        val errorColor: Color,
        val disabledColor: Color
    )

    val default: Style
        @Composable get() = with(ProcessOutTheme) {
            Style(
                normalColor = colors.input.borderDefault,
                selectedColor = colors.button.primaryBackgroundDefault,
                errorColor = colors.input.borderError,
                disabledColor = colors.input.borderDisabled
            )
        }

    private val MaterialRadioButtonSize = 20.dp
    internal val RadioButtonSize = 22.dp
    internal val RadioButtonScale = RadioButtonSize.value / MaterialRadioButtonSize.value

    @Composable
    internal fun colors(
        style: Style,
        isError: Boolean
    ): RadioButtonColors {
        val selectedColor: Color
        val unselectedColor: Color
        if (isError) {
            selectedColor = style.errorColor
            unselectedColor = style.errorColor
        } else {
            selectedColor = style.selectedColor
            unselectedColor = style.normalColor
        }
        return RadioButtonDefaults.colors(
            selectedColor = selectedColor,
            unselectedColor = unselectedColor,
            disabledSelectedColor = style.disabledColor,
            disabledUnselectedColor = style.disabledColor
        )
    }
}
