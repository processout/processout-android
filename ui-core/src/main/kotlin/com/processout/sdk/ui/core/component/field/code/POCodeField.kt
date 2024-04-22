package com.processout.sdk.ui.core.component.field.code

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.text.POTextField
import com.processout.sdk.ui.core.theme.ProcessOutTheme

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POCodeField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = POCodeField.DefaultLength,
    style: POField.Style = POCodeField.default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Row(
        modifier = Modifier.focusGroup(),
        horizontalArrangement = Arrangement.spacedBy(ProcessOutTheme.spacing.small)
    ) {
        val focusManager = LocalFocusManager.current
        var values by rememberDefaultValues(value, length)
        for (textFieldIndex in 0..<length) {
            POTextField(
                value = values.getOrNull(textFieldIndex) ?: TextFieldValue(),
                onValueChange = {
                    values = values.mapIndexed { valueIndex, textFieldValue ->
                        if (valueIndex == textFieldIndex) {
                            val updatedText = it.text.find { it.isDigit() }?.toString() ?: String()
                            val isTextChanged = textFieldValue.text != updatedText
                            TextFieldValue(
                                text = updatedText,
                                selection = if (isTextChanged) {
                                    TextRange(updatedText.length)
                                } else {
                                    it.selection
                                }
                            )
                        } else {
                            textFieldValue.copy()
                        }
                    }
                    val updatedText = values.joinToString(separator = String()) { it.text }
                    onValueChange(TextFieldValue(text = updatedText))
                },
                modifier = modifier
                    .requiredWidth(ProcessOutTheme.dimensions.formComponentHeight)
                    .onPreviewKeyEvent {
                        when {
                            it.key == Key.Backspace && it.type == KeyEventType.KeyUp -> {
                                if (textFieldIndex != 0) {
                                    focusManager.moveFocus(FocusDirection.Previous)
                                }
                                false
                            }
                            else -> {
                                if (it.type == KeyEventType.KeyUp) {
                                    if (textFieldIndex != length - 1) {
                                        focusManager.moveFocus(FocusDirection.Next)
                                    }
                                }
                                false
                            }
                        }
                    },
                style = style,
                keyboardOptions = keyboardOptions,
                keyboardActions = KeyboardActions(
                    onDone = keyboardActions.onDone,
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )
            )
        }
    }
}

@Composable
private fun rememberDefaultValues(
    value: TextFieldValue,
    length: Int
): MutableState<List<TextFieldValue>> = remember {
    val values = if (value.text.isEmpty()) {
        val list = mutableListOf<TextFieldValue>()
        for (i in 0..<length) {
            list.add(TextFieldValue())
        }
        list
    } else {
        val filteredText = value.text.replace(Regex("\\D"), String()).take(length)
        var list = filteredText.map {
            val text = if (it.isDigit()) it.toString() else String()
            TextFieldValue(
                text = text,
                selection = TextRange(text.length)
            )
        }
        if (list.size < length) {
            val lengthDiff = length - list.size
            val mutableList = list.toMutableList()
            for (i in 0..<lengthDiff) {
                mutableList.add(TextFieldValue())
            }
            list = mutableList
        }
        list
    }
    mutableStateOf(values)
}

/** @suppress */
@ProcessOutInternalApi
object POCodeField {

    internal val DefaultLength = 6

    val default: POField.Style
        @Composable get() = with(POField.default) {
            copy(
                normal = normal.copy(
                    text = normal.text.copy(
                        textStyle = ProcessOutTheme.typography.medium.title.copy(
                            textAlign = TextAlign.Center
                        )
                    )
                ),
                error = error.copy(
                    text = error.text.copy(
                        textStyle = ProcessOutTheme.typography.medium.title.copy(
                            textAlign = TextAlign.Center
                        )
                    )
                )
            )
        }
}
