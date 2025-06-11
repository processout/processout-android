package com.processout.sdk.ui.core.component.field.checkbox

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.field.LabeledFieldLayout
import com.processout.sdk.ui.core.component.field.POFieldLabels

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POLabeledCheckboxField(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String?,
    description: String?,
    modifier: Modifier = Modifier,
    checkboxStyle: POCheckbox.Style = POCheckbox.default,
    labelsStyle: POFieldLabels.Style = POFieldLabels.default,
    enabled: Boolean = true,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    LabeledFieldLayout(
        title = title,
        description = description,
        style = labelsStyle
    ) {
        POCheckbox(
            text = text,
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = modifier,
            minHeight = 30.dp,
            style = checkboxStyle,
            enabled = enabled,
            isError = isError,
            interactionSource = interactionSource
        )
    }
}
