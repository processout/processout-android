package com.processout.sdk.ui.core.component.field.radio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup.textPaddingTop
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup.textStyle
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup.toRadioButtonStyle
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
                    .requiredHeightIn(min = ProcessOutTheme.dimensions.formComponentMinHeight)
                    .clickable(
                        onClick = onClick,
                        interactionSource = interactionSource,
                        indication = null
                    )
            ) {
                val selected = it.value == value
                PORadioButton(
                    selected = selected,
                    onClick = onClick,
                    style = style.toRadioButtonStyle(),
                    isError = isError
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
        val error: StateStyle,
        val disabled: StateStyle
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
                    buttonColor = colors.input.borderDefault,
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
                    buttonColor = colors.input.borderError,
                    text = POText.Style(
                        color = colors.text.primary,
                        textStyle = typography.label2
                    )
                ),
                disabled = StateStyle(
                    buttonColor = colors.input.borderDisabled,
                    text = POText.Style(
                        color = colors.text.primary,
                        textStyle = typography.label2
                    )
                )
            )
        }

    @Composable
    fun custom(style: PORadioButtonStyle): Style {
        val normal = style.normal.toStateStyle()
        return Style(
            normal = normal,
            selected = style.selected.toStateStyle(),
            error = style.error.toStateStyle(),
            disabled = style.disabled?.toStateStyle() ?: normal
        )
    }

    @Composable
    private fun PORadioButtonStateStyle.toStateStyle() = StateStyle(
        buttonColor = colorResource(id = buttonColorResId),
        text = POText.custom(style = text)
    )

    internal fun Style.toRadioButtonStyle() = PORadioButton.Style(
        normalColor = normal.buttonColor,
        selectedColor = selected.buttonColor,
        errorColor = error.buttonColor,
        disabledColor = disabled.buttonColor
    )

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
        val formComponentHeight = ProcessOutTheme.dimensions.formComponentMinHeight
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
