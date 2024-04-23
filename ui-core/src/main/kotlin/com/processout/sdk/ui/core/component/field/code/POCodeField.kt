@file:Suppress("MayBeConstant")

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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.Lifecycle
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.PORequestFocus
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.text.POTextField
import com.processout.sdk.ui.core.component.texttoolbar.ProcessOutTextToolbar
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
    isFocused: Boolean = false,
    lifecycleEvent: Lifecycle.Event? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Row(
        modifier = Modifier.focusGroup(),
        horizontalArrangement = Arrangement.spacedBy(ProcessOutTheme.spacing.small)
    ) {
        var values by remember { mutableStateOf(values(value, length)) }
        var focusedIndex by remember { mutableIntStateOf(values.focusedIndex()) }
        val clipboardManager = LocalClipboardManager.current
        CompositionLocalProvider(
            LocalTextToolbar provides ProcessOutTextToolbar(
                view = LocalView.current,
                onPasteRequested = {
                    if (clipboardManager.hasText()) {
                        values = values(TextFieldValue(text = clipboardManager.getText()?.text ?: String()), length)
                        focusedIndex = values.focusedIndex()
                        onValueChange(values.textFieldValue())
                    }
                },
                hideUnspecifiedActions = true
            )
        ) {
            val focusManager = LocalFocusManager.current
            for (textFieldIndex in 0..<length) {
                val focusRequester = remember { FocusRequester() }
                POTextField(
                    value = values.getOrNull(textFieldIndex) ?: TextFieldValue(),
                    onValueChange = {
                        if (it.selection.length == 0) {
                            values = values.mapIndexed { valueIndex, textFieldValue ->
                                if (valueIndex == textFieldIndex) {
                                    val updatedText = it.text.find { char -> char.isDigit() }?.toString() ?: String()
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
                            onValueChange(values.textFieldValue())
                        }
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
                        }
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (it.isFocused) {
                                focusedIndex = textFieldIndex
                            }
                        },
                    style = style,
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions
                )
                if (isFocused && textFieldIndex == focusedIndex) {
                    if (lifecycleEvent == Lifecycle.Event.ON_RESUME) {
                        PORequestFocus(focusRequester, lifecycleEvent)
                    } else {
                        PORequestFocus(focusRequester)
                    }
                }
            }
        }
    }
}

private fun values(
    value: TextFieldValue,
    length: Int
): List<TextFieldValue> {
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
    return values
}

private fun List<TextFieldValue>.textFieldValue() = TextFieldValue(
    text = joinToString(separator = String()) { it.text }
)

private fun List<TextFieldValue>.focusedIndex(): Int {
    forEachIndexed { index, textFieldValue ->
        if (textFieldValue.text.isEmpty()) {
            return index
        }
    }
    return lastIndex
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
