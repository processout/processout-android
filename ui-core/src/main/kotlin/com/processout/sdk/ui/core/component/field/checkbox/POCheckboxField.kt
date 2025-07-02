package com.processout.sdk.ui.core.component.field.checkbox

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POMessageBox
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POCheckboxField(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    checkboxStyle: POCheckbox.Style = POCheckbox.default2,
    descriptionStyle: POMessageBox.Style = POMessageBox.error2,
    enabled: Boolean = true,
    isError: Boolean = false,
    description: String? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Column(modifier = modifier) {
        POCheckbox(
            text = text,
            checked = checked,
            onCheckedChange = onCheckedChange,
            minHeight = 40.dp,
            checkboxSize = 16.dp,
            rowShape = shapes.roundedCorners6,
            style = checkboxStyle,
            enabled = enabled,
            isError = isError,
            interactionSource = interactionSource
        )
        POMessageBox(
            text = description,
            style = descriptionStyle
        )
    }
}
