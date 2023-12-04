@file:OptIn(ExperimentalMaterial3Api::class)

package com.processout.sdk.ui.core.component.field

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.theme.ProcessOutTheme

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    style: POField.Style = POField.default,
    enabled: Boolean = true,
    isError: Boolean = false,
    forceTextDirectionLtr: Boolean = false,
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
    CompositionLocalProvider(
        LocalTextSelectionColors provides POField.textSelectionColors(isError = isError, style = style)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.height(ProcessOutTheme.dimensions.formComponentHeight),
            enabled = enabled,
            textStyle = POField.textStyle(
                isError = isError,
                forceTextDirectionLtr = forceTextDirectionLtr,
                style = style
            ),
            cursorBrush = POField.cursorBrush(isError = isError, style = style),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            decorationBox = @Composable { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value,
                    innerTextField = innerTextField,
                    enabled = enabled,
                    isError = isError,
                    placeholder = {
                        if (!placeholderText.isNullOrBlank()) POField.Placeholder(
                            text = placeholderText,
                            isError = isError,
                            style = style
                        )
                    },
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    singleLine = singleLine,
                    visualTransformation = visualTransformation,
                    interactionSource = interactionSource,
                    contentPadding = POField.contentPadding,
                    container = {
                        POField.ContainerBox(
                            isError = isError,
                            style = style
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
            value = "test@gmail.com",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
