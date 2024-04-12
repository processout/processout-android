package com.processout.sdk.ui.core.component.field

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.state.POAvailableValue
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.theme.ProcessOutTheme

/** @suppress */
@ProcessOutInternalApi
@Composable
fun PORadioGroup(
    value: String,
    onValueChange: (String) -> Unit,
    availableValues: POImmutableList<POAvailableValue>,
    modifier: Modifier = Modifier,
    style: PORadioGroup.Style = PORadioGroup.default,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Column(
        modifier = modifier
    ) {
        availableValues.elements.forEach {
            val onClick = remember { { onValueChange(it.value) } }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeightIn(min = ProcessOutTheme.dimensions.formComponentHeight)
                    .clickable(
                        onClick = onClick,
                        interactionSource = interactionSource,
                        indication = null
                    )
            ) {
                val selected = it.value == value
                RadioButton(
                    selected = selected,
                    onClick = onClick,
                    modifier = Modifier
                        .requiredWidth(ProcessOutTheme.dimensions.radioButtonSize)
                        .requiredHeight(ProcessOutTheme.dimensions.formComponentHeight),
                    colors = PORadioGroup.buttonColors(style = style, isError = isError)
                )
                val textStyle = PORadioGroup.textStyle(style = style, selected = selected, isError = isError)
                POText(
                    text = it.text,
                    modifier = Modifier.padding(start = 10.dp, top = PORadioGroup.textPaddingTop(textStyle)),
                    color = textStyle.color,
                    style = textStyle.textStyle
                )
            }
        }
    }
}

/** @suppress */
@ProcessOutInternalApi
object PORadioGroup {

    @Immutable
    data class Style(
        val normal: StateStyle,
        val selected: StateStyle,
        val error: StateStyle
    )

    @Immutable
    data class StateStyle(
        val buttonColor: Color,
        val text: POText.Style
    )

    val default: Style
        @Composable get() = with(ProcessOutTheme) {
            Style(
                normal = StateStyle(
                    buttonColor = colors.border.default,
                    text = POText.Style(
                        color = colors.text.primary,
                        textStyle = typography.fixed.label
                    )
                ),
                selected = StateStyle(
                    buttonColor = colors.action.primaryDefault,
                    text = POText.Style(
                        color = colors.text.primary,
                        textStyle = typography.fixed.label
                    )
                ),
                error = StateStyle(
                    buttonColor = colors.text.error,
                    text = POText.Style(
                        color = colors.text.primary,
                        textStyle = typography.fixed.label
                    )
                )
            )
        }

    @Composable
    internal fun buttonColors(
        style: Style,
        isError: Boolean
    ): RadioButtonColors {
        val selectedColor: Color
        val unselectedColor: Color
        if (isError) {
            selectedColor = style.error.buttonColor
            unselectedColor = style.error.buttonColor
        } else {
            selectedColor = style.selected.buttonColor
            unselectedColor = style.normal.buttonColor
        }
        return RadioButtonDefaults.colors(
            selectedColor = selectedColor,
            unselectedColor = unselectedColor
        )
    }

    internal fun textStyle(
        style: Style,
        selected: Boolean,
        isError: Boolean
    ): POText.Style =
        if (isError) style.error.text
        else if (selected) style.selected.text
        else style.normal.text

    @Composable
    internal fun textPaddingTop(style: POText.Style): Dp {
        val textMeasurer = rememberTextMeasurer()
        val singleLineTextMeasurement = remember(style) {
            textMeasurer.measure(text = String(), style = style.textStyle)
        }
        val density = LocalDensity.current
        val formComponentHeight = ProcessOutTheme.dimensions.formComponentHeight
        return remember(singleLineTextMeasurement) {
            with(density) {
                val formComponentCenter = formComponentHeight / 2
                val singleLineTextCenter = singleLineTextMeasurement.size.height.toDp() / 2
                val paddingTop = formComponentCenter - singleLineTextCenter + 1.dp
                if (paddingTop.value > 0) paddingTop else 0.dp
            }
        }
    }
}
