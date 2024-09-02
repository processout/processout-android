package com.processout.sdk.ui.core.component.field.radio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.POText.measuredPaddingTop
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup.textStyle
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup.toRadioButtonStyle
import com.processout.sdk.ui.core.state.POAvailableValue
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.style.PORadioButtonStateStyle
import com.processout.sdk.ui.core.style.PORadioButtonStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.typography

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
                    .requiredHeightIn(min = dimensions.formComponentMinHeight)
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
                    modifier = Modifier.padding(
                        start = 10.dp,
                        top = measuredPaddingTop(
                            textStyle = textStyle.textStyle,
                            componentHeight = dimensions.formComponentMinHeight
                        )
                    ),
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
        @Composable get() = Style(
            normal = StateStyle(
                buttonColor = colors.input.borderDefault,
                text = POText.label1
            ),
            selected = StateStyle(
                buttonColor = colors.button.primaryBackgroundDefault,
                text = POText.label1
            ),
            error = StateStyle(
                buttonColor = colors.input.borderError,
                text = POText.label1
            ),
            disabled = StateStyle(
                buttonColor = colors.input.borderDisabled,
                text = POText.Style(
                    color = colors.text.disabled,
                    textStyle = typography.label1
                )
            )
        )

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

    fun Style.toRadioButtonStyle() = PORadioButton.Style(
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
}
