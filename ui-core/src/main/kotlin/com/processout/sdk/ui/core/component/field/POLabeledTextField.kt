package com.processout.sdk.ui.core.component.field

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.theme.ProcessOutTheme

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POLabeledTextField(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    fieldStyle: POFieldStyle = POFieldDefaults.default,
    labelsStyle: POFieldLabelsStyle = POFieldLabelsDefaults.default,
    enabled: Boolean = true,
    isError: Boolean = false,
    description: String = String(),
    placeholderText: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Column {
        POText(
            text = title,
            modifier = Modifier.padding(bottom = ProcessOutTheme.spacing.small),
            color = if (isError) labelsStyle.error.title.color else labelsStyle.normal.title.color,
            style = if (isError) labelsStyle.error.title.textStyle else labelsStyle.normal.title.textStyle
        )
        POTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            style = fieldStyle,
            enabled = enabled,
            isError = isError,
            placeholderText = placeholderText,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource
        )
        POText(
            text = description,
            modifier = Modifier.padding(top = ProcessOutTheme.spacing.small),
            color = if (isError) labelsStyle.error.description.color else labelsStyle.normal.description.color,
            style = if (isError) labelsStyle.error.description.textStyle else labelsStyle.normal.description.textStyle
        )
    }
}
