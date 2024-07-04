@file:OptIn(ExperimentalMaterial3Api::class)

package com.processout.sdk.ui.core.component.field.text

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.POField.ContainerBox
import com.processout.sdk.ui.core.component.field.POField.stateStyle
import com.processout.sdk.ui.core.component.field.POField.textSelectionColors
import com.processout.sdk.ui.core.component.field.POField.textStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = POField.contentPadding,
    style: POField.Style = POField.default,
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
    var isFocused by remember { mutableStateOf(false) }
    val stateStyle = style.stateStyle(isError = isError, isFocused = isFocused)
    CompositionLocalProvider(
        LocalTextSelectionColors provides textSelectionColors(stateStyle.controlsTintColor)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .requiredHeight(ProcessOutTheme.dimensions.formComponentMinHeight)
                .onFocusChanged {
                    isFocused = it.isFocused
                },
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle(
                style = stateStyle.text,
                forceTextDirectionLtr = forceTextDirectionLtr
            ),
            cursorBrush = SolidColor(stateStyle.controlsTintColor),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            decorationBox = @Composable { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value.text,
                    innerTextField = innerTextField,
                    enabled = enabled,
                    isError = isError,
                    placeholder = {
                        if (!placeholderText.isNullOrBlank()) {
                            POText(
                                text = placeholderText,
                                color = stateStyle.placeholderTextColor,
                                style = stateStyle.text.textStyle
                            )
                        }
                    },
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    singleLine = singleLine,
                    visualTransformation = visualTransformation,
                    interactionSource = interactionSource,
                    contentPadding = contentPadding,
                    container = {
                        ContainerBox(
                            style = stateStyle,
                            isDropdown = isDropdown
                        )
                    }
                )
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun POTextFieldPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        POTextField(
            value = TextFieldValue(text = "test@gmail.com"),
            onValueChange = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
