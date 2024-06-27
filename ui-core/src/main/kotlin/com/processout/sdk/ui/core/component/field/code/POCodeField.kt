@file:Suppress("MayBeConstant", "MemberVisibilityCanBePrivate")

package com.processout.sdk.ui.core.component.field.code

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.Lifecycle
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.PORequestFocus
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.code.POCodeField.style
import com.processout.sdk.ui.core.component.field.code.POCodeField.validLength
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
    style: POField.Style = POCodeField.default,
    length: Int = POCodeField.LengthMax,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    enabled: Boolean = true,
    isError: Boolean = false,
    isFocused: Boolean = false,
    lifecycleEvent: Lifecycle.Event? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .focusGroup(),
            horizontalArrangement = Arrangement.spacedBy(
                space = ProcessOutTheme.spacing.small,
                alignment = horizontalAlignment
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val validLength by remember { mutableIntStateOf(validLength(length)) }
            var values by remember { mutableStateOf(values(value.text, validLength)) }
            var focusedIndex by remember { mutableIntStateOf(values.focusedIndex()) }
            val clipboardManager = LocalClipboardManager.current
            CompositionLocalProvider(
                LocalTextToolbar provides ProcessOutTextToolbar(
                    view = LocalView.current,
                    onPasteRequested = {
                        if (clipboardManager.hasText()) {
                            val pastedValues = values(
                                text = clipboardManager.getText()?.text ?: String(),
                                length = validLength
                            )
                            if (!pastedValues.all { it.text.isEmpty() }) {
                                values = pastedValues
                                focusedIndex = values.focusedIndex()
                                onValueChange(values.textFieldValue())
                            }
                        }
                    },
                    hideUnspecifiedActions = true
                )
            ) {
                val focusManager = LocalFocusManager.current
                for (textFieldIndex in values.indices) {
                    val focusRequester = remember { FocusRequester() }
                    POTextField(
                        value = values.getOrNull(textFieldIndex) ?: TextFieldValue(),
                        onValueChange = { updatedValue ->
                            if (updatedValue.selection.length == 0) {
                                val filteredText = updatedValue.text.filter { it.isDigit() }
                                values = values.mapIndexed { valueIndex, textFieldValue ->
                                    if (valueIndex == textFieldIndex) {
                                        val updatedText = filteredText.firstOrNull()?.toString() ?: String()
                                        val isTextChanged = textFieldValue.text != updatedText
                                        TextFieldValue(
                                            text = updatedText,
                                            selection = if (isTextChanged) {
                                                TextRange(updatedText.length)
                                            } else {
                                                updatedValue.selection
                                            }
                                        )
                                    } else {
                                        textFieldValue.copy(selection = TextRange.Zero)
                                    }
                                }
                                if (textFieldIndex != values.lastIndex &&
                                    filteredText.length == 2 &&
                                    updatedValue.selection.start == 2
                                ) {
                                    val nextText = filteredText.last().toString()
                                    values = values.mapIndexed { index, textFieldValue ->
                                        if (index == textFieldIndex + 1) {
                                            TextFieldValue(
                                                text = nextText,
                                                selection = TextRange(nextText.length)
                                            )
                                        } else {
                                            textFieldValue.copy(selection = TextRange.Zero)
                                        }
                                    }
                                }
                                onValueChange(values.textFieldValue())
                            }
                        },
                        modifier = modifier
                            .requiredWidth(ProcessOutTheme.dimensions.interactiveComponentMinSize)
                            .onPreviewKeyEvent {
                                when {
                                    it.key == Key.Backspace && it.type == KeyEventType.KeyDown -> {
                                        if (textFieldIndex != 0 && values[textFieldIndex].selection.start == 0) {
                                            values = values.mapIndexed { index, textFieldValue ->
                                                if (index == textFieldIndex - 1) {
                                                    TextFieldValue()
                                                } else {
                                                    textFieldValue.copy(selection = TextRange.Zero)
                                                }
                                            }
                                            focusManager.moveFocus(FocusDirection.Previous)
                                            onValueChange(values.textFieldValue())
                                        }
                                        false
                                    }
                                    else -> {
                                        if (it.type == KeyEventType.KeyDown && textFieldIndex != values.lastIndex) {
                                            focusManager.moveFocus(FocusDirection.Next)
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
                        style = style(style),
                        enabled = enabled,
                        isError = isError,
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
}

private fun values(
    text: String,
    length: Int
): List<TextFieldValue> {
    val values = mutableListOf<TextFieldValue>()
    while (values.size < length) {
        values.add(TextFieldValue())
    }
    val filteredText = text.filter { it.isDigit() }.take(length)
    filteredText.forEachIndexed { index, char ->
        values[index] = TextFieldValue(
            text = char.toString(),
            selection = TextRange(char.toString().length)
        )
    }
    return values
}

private fun List<TextFieldValue>.focusedIndex(): Int {
    forEachIndexed { index, textFieldValue ->
        if (textFieldValue.text.isEmpty()) {
            return index
        }
    }
    return lastIndex
}

private fun List<TextFieldValue>.textFieldValue() = TextFieldValue(
    text = joinToString(separator = String()) { it.text }
)

/** @suppress */
@ProcessOutInternalApi
object POCodeField {

    val default: POField.Style
        @Composable get() = with(POField.default) {
            copy(
                normal = normal.copy(
                    text = normal.text.copy(
                        textStyle = ProcessOutTheme.typography.title
                    )
                ),
                error = error.copy(
                    text = error.text.copy(
                        textStyle = ProcessOutTheme.typography.title
                    )
                )
            )
        }

    internal fun style(style: POField.Style) = with(style) {
        copy(
            normal = normal.copy(
                text = normal.text.copy(
                    textStyle = normal.text.textStyle.copy(
                        textAlign = TextAlign.Center
                    )
                )
            ),
            error = error.copy(
                text = error.text.copy(
                    textStyle = error.text.textStyle.copy(
                        textAlign = TextAlign.Center
                    )
                )
            )
        )
    }

    val LengthMin = 1
    val LengthMax = 6

    internal fun validLength(length: Int): Int {
        if (length in LengthMin..LengthMax) {
            return length
        }
        return LengthMax
    }
}
