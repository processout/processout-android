package com.processout.sdk.ui.core.component.field.text

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POTextField2(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = dimensions.fieldHeight,
    contentPadding: PaddingValues = POField.contentPadding,
    style: POField.Style = POField.default,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isDropdown: Boolean = false,
    isError: Boolean = false,
    forceTextDirectionLtr: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
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
    POTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        height = height,
        contentPadding = contentPadding,
        style = style,
        enabled = enabled,
        readOnly = readOnly,
        isDropdown = isDropdown,
        isError = isError,
        forceTextDirectionLtr = forceTextDirectionLtr,
        label = label,
        placeholder = placeholder,
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
