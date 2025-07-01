package com.processout.sdk.ui.core.component.field.radio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POBorderStroke
import com.processout.sdk.ui.core.component.POMessageBox
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.POText.measuredPaddingTop
import com.processout.sdk.ui.core.component.field.radio.PORadioField.optionStateStyle
import com.processout.sdk.ui.core.component.field.radio.PORadioField.radioButtonStyle
import com.processout.sdk.ui.core.component.field.radio.PORadioField.stateStyle
import com.processout.sdk.ui.core.state.POAvailableValue
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.core.theme.ProcessOutTheme.typography

/** @suppress */
@ProcessOutInternalApi
@Composable
fun PORadioField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    availableValues: POImmutableList<POAvailableValue>,
    modifier: Modifier = Modifier,
    fieldStyle: PORadioField.Style = PORadioField.default,
    descriptionStyle: POMessageBox.Style = POMessageBox.error2,
    title: String? = null,
    description: String? = null,
    isError: Boolean = false
) {
    Column(modifier = modifier) {
        val stateStyle = stateStyle(
            style = fieldStyle,
            isSelected = value.text.isNotBlank(),
            isError = isError
        )
        if (!title.isNullOrBlank()) {
            POText(
                text = title,
                modifier = Modifier.padding(bottom = spacing.space12),
                color = stateStyle.title.color,
                style = stateStyle.title.textStyle
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = stateStyle.border.width,
                    color = stateStyle.border.color,
                    shape = stateStyle.shape
                )
                .padding(spacing.space4),
            verticalArrangement = Arrangement.spacedBy(spacing.space2)
        ) {
            availableValues.elements.forEach { availableValue ->
                val isSelected = availableValue.value == value.text
                val optionStateStyle = optionStateStyle(
                    style = fieldStyle,
                    isSelected = isSelected,
                    isError = isError
                )
                val onClick = { onValueChange(TextFieldValue(text = availableValue.value)) }
                val interactionSource = remember { MutableInteractionSource() }
                val rowMinHeight = dimensions.formComponentMinHeight
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeightIn(min = rowMinHeight)
                        .clip(shape = shapes.roundedCorners4)
                        .clickable(
                            onClick = onClick,
                            interactionSource = interactionSource,
                            indication = optionStateStyle.rowRippleColor?.let { ripple(color = it) }
                        )
                        .background(optionStateStyle.rowBackgroundColor)
                        .padding(start = spacing.space12)
                ) {
                    PORadioButton(
                        selected = isSelected,
                        onClick = onClick,
                        radioButtonSize = 16.dp,
                        style = fieldStyle.radioButtonStyle(),
                        isError = isError
                    )
                    POText(
                        text = availableValue.text,
                        modifier = Modifier.padding(
                            start = spacing.space10,
                            top = measuredPaddingTop(
                                textStyle = optionStateStyle.option.textStyle,
                                componentHeight = rowMinHeight
                            ),
                            bottom = spacing.space10
                        ),
                        color = optionStateStyle.option.color,
                        style = optionStateStyle.option.textStyle
                    )
                }
            }
        }
        POMessageBox(
            text = description,
            modifier = Modifier.padding(top = spacing.space12),
            style = descriptionStyle
        )
    }
}

/** @suppress */
@ProcessOutInternalApi
object PORadioField {

    @Immutable
    data class Style(
        val normal: StateStyle,
        val selected: StateStyle,
        val error: StateStyle,
        val disabled: StateStyle
    )

    @Immutable
    data class StateStyle(
        val title: POText.Style,
        val option: POText.Style,
        val radioButtonColor: Color,
        val rowBackgroundColor: Color,
        val rowRippleColor: Color?,
        val shape: Shape,
        val border: POBorderStroke
    )

    val default: Style
        @Composable get() = Style(
            normal = StateStyle(
                title = POText.Style(
                    color = colors.text.primary,
                    textStyle = typography.s16(FontWeight.Medium)
                ),
                option = POText.Style(
                    color = colors.text.secondary,
                    textStyle = typography.s15(FontWeight.Medium)
                ),
                radioButtonColor = PORadioButton.default2.normalColor,
                rowBackgroundColor = colors.surface.default,
                rowRippleColor = colors.surface.darkoutRipple,
                shape = shapes.roundedCorners6,
                border = POBorderStroke(width = 1.5.dp, color = colors.input.borderDefault2)
            ),
            selected = StateStyle(
                title = POText.Style(
                    color = colors.text.primary,
                    textStyle = typography.s16(FontWeight.Medium)
                ),
                option = POText.Style(
                    color = colors.text.primary,
                    textStyle = typography.s15(FontWeight.Medium)
                ),
                radioButtonColor = PORadioButton.default2.selectedColor,
                rowBackgroundColor = colors.surface.darkout,
                rowRippleColor = null,
                shape = shapes.roundedCorners6,
                border = POBorderStroke(width = 1.5.dp, color = colors.input.borderDefault2)
            ),
            error = StateStyle(
                title = POText.Style(
                    color = colors.text.error,
                    textStyle = typography.s16(FontWeight.Medium)
                ),
                option = POText.Style(
                    color = colors.text.secondary,
                    textStyle = typography.s15(FontWeight.Medium)
                ),
                radioButtonColor = PORadioButton.default2.errorColor,
                rowBackgroundColor = colors.surface.default,
                rowRippleColor = colors.surface.darkoutRipple,
                shape = shapes.roundedCorners6,
                border = POBorderStroke(width = 1.5.dp, color = colors.input.borderError)
            ),
            disabled = StateStyle(
                title = POText.Style(
                    color = colors.text.primary,
                    textStyle = typography.s16(FontWeight.Medium)
                ),
                option = POText.Style(
                    color = colors.text.disabled,
                    textStyle = typography.s15(FontWeight.Medium)
                ),
                radioButtonColor = PORadioButton.default2.disabledColor,
                rowBackgroundColor = colors.surface.default,
                rowRippleColor = null,
                shape = shapes.roundedCorners6,
                border = POBorderStroke(width = 1.5.dp, color = colors.input.borderDefault2)
            )
        )

    fun stateStyle(
        style: Style,
        isSelected: Boolean,
        isError: Boolean
    ): StateStyle =
        if (isError) style.error
        else if (isSelected) style.selected
        else style.normal

    fun optionStateStyle(
        style: Style,
        isSelected: Boolean,
        isError: Boolean
    ): StateStyle =
        if (isSelected) style.selected
        else if (isError) style.error
        else style.normal

    fun Style.radioButtonStyle() = PORadioButton.Style(
        normalColor = normal.radioButtonColor,
        selectedColor = selected.radioButtonColor,
        errorColor = error.radioButtonColor,
        disabledColor = disabled.radioButtonColor
    )
}
