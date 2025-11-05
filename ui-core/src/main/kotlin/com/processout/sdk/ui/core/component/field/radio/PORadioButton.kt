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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.field.radio.PORadioButton.MaterialRadioButtonSize
import com.processout.sdk.ui.core.component.field.radio.PORadioButton.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions

/** @suppress */
@ProcessOutInternalApi
@Composable
fun PORadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    radioButtonSize: Dp = MaterialRadioButtonSize,
    style: PORadioButton.Style = PORadioButton.default,
    enabled: Boolean = true,
    isError: Boolean = false
) {
    val radioButtonScale = radioButtonSize.value / MaterialRadioButtonSize.value
    RadioButton(
        selected = selected,
        onClick = onClick,
        modifier = modifier
            .scale(radioButtonScale)
            .requiredWidth(radioButtonSize)
            .requiredHeight(dimensions.formComponentMinHeight),
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
        @Composable get() = Style(
            normalColor = colors.checkRadio.borderDefault,
            selectedColor = colors.checkRadio.borderActive,
            errorColor = colors.checkRadio.borderError,
            disabledColor = colors.checkRadio.iconDisabled
        )

    internal val MaterialRadioButtonSize = 20.dp

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
