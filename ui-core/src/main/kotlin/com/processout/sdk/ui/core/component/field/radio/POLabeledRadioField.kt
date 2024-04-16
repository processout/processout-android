package com.processout.sdk.ui.core.component.field.radio

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.field.LabeledFieldLayout
import com.processout.sdk.ui.core.component.field.POFieldLabels
import com.processout.sdk.ui.core.state.POAvailableValue
import com.processout.sdk.ui.core.state.POImmutableList

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POLabeledRadioField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    availableValues: POImmutableList<POAvailableValue>,
    title: String,
    description: String?,
    modifier: Modifier = Modifier,
    radioGroupStyle: PORadioGroup.Style = PORadioGroup.default,
    labelsStyle: POFieldLabels.Style = POFieldLabels.default,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    LabeledFieldLayout(
        title = title,
        description = description,
        style = labelsStyle
    ) {
        PORadioGroup(
            value = value.text,
            onValueChange = { onValueChange(TextFieldValue(text = it)) },
            availableValues = availableValues,
            modifier = modifier,
            style = radioGroupStyle,
            isError = isError,
            interactionSource = interactionSource
        )
    }
}
