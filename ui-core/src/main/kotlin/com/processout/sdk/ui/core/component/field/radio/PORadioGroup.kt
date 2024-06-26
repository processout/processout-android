package com.processout.sdk.ui.core.component.field.radio

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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup.RadioButtonScale
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup.RadioButtonSize
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup.buttonColors
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup.textPaddingTop
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup.textStyle
import com.processout.sdk.ui.core.state.POAvailableValue
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.style.PORadioButtonStateStyle
import com.processout.sdk.ui.core.style.PORadioButtonStyle
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
                    .requiredHeightIn(min = ProcessOutTheme.dimensions.formComponentMinSize)
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
                        .scale(RadioButtonScale)
                        .requiredWidth(RadioButtonSize)
                        .requiredHeight(ProcessOutTheme.dimensions.formComponentMinSize),
                    colors = buttonColors(style = style, isError = isError)
                )
                val textStyle = textStyle(style = style, selected = selected, isError = isError)
                POText(
                    text = it.text,
                    modifier = Modifier.padding(start = 10.dp, top = textPaddingTop(textStyle)),
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
                        textStyle = typography.label2
                    )
                ),
                selected = StateStyle(
                    buttonColor = colors.button.primaryBackgroundDefault,
                    text = POText.Style(
                        color = colors.text.primary,
                        textStyle = typography.label2
                    )
                ),
                error = StateStyle(
                    buttonColor = colors.text.error,
                    text = POText.Style(
                        color = colors.text.primary,
                        textStyle = typography.label2
                    )
                )
            )
        }

    @Composable
    fun custom(style: PORadioButtonStyle) = Style(
        normal = style.normal.toStateStyle(),
        selected = style.selected.toStateStyle(),
        error = style.error.toStateStyle()
    )

    @Composable
    private fun PORadioButtonStateStyle.toStateStyle() = StateStyle(
        buttonColor = colorResource(id = buttonColorResId),
        text = POText.custom(style = text)
    )

    private val MaterialRadioButtonSize = 20.dp
    internal val RadioButtonSize = 18.dp
    internal val RadioButtonScale = RadioButtonSize.value / MaterialRadioButtonSize.value

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
        val formComponentHeight = ProcessOutTheme.dimensions.formComponentMinSize
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
