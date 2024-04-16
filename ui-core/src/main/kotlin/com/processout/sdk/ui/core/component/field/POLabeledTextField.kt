package com.processout.sdk.ui.core.component.field

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POLabeledTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    title: String,
    description: String?,
    modifier: Modifier = Modifier,
    fieldStyle: POField.Style = POField.default,
    labelsStyle: POFieldLabels.Style = POFieldLabels.default,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isDropdown: Boolean = false,
    isError: Boolean = false,
    forceTextDirectionLtr: Boolean = false,
    placeholderText: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    LabeledFieldLayout(
        title = title,
        description = description,
        style = labelsStyle
    ) {
        POTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            style = fieldStyle,
            enabled = enabled,
            readOnly = readOnly,
            isDropdown = isDropdown,
            isError = isError,
            forceTextDirectionLtr = forceTextDirectionLtr,
            placeholderText = placeholderText,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            interactionSource = interactionSource
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun POLabeledTextFieldPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        POLabeledTextField(
            title = "Title",
            value = TextFieldValue(text = "test@gmail.com"),
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            isError = true,
            description = "This is error message."
        )
    }
}
