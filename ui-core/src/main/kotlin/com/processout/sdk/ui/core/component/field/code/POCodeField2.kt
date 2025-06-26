package com.processout.sdk.ui.core.component.field.code

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.Lifecycle
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POMessageBox
import com.processout.sdk.ui.core.component.PORequestFocus
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.code.POCodeField.align
import com.processout.sdk.ui.core.component.field.code.POCodeField.rememberTextFieldWidth
import com.processout.sdk.ui.core.component.field.code.POCodeField.validLength
import com.processout.sdk.ui.core.component.field.text.POTextField2
import com.processout.sdk.ui.core.component.texttoolbar.ProcessOutTextToolbar
import com.processout.sdk.ui.core.state.POInputFilter
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POCodeField2(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    fieldStyle: POField.Style = POCodeField.default, // TODO(v2)
    descriptionStyle: POMessageBox.Style = POMessageBox.error2,
    length: Int = POCodeField.LengthMax,
    label: String? = null,
    description: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    isFocused: Boolean = false,
    lifecycleEvent: Lifecycle.Event? = null,
    inputFilter: POInputFilter? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Column(modifier = modifier) {
        if (label != null) {
            // TODO(v2): color and style
            POText(
                text = label,
                modifier = Modifier.padding(bottom = spacing.space12)
            )
        }
        Code(
            value = value,
            onValueChange = onValueChange,
            style = fieldStyle,
            length = length,
            enabled = enabled,
            isError = isError,
            isFocused = isFocused,
            lifecycleEvent = lifecycleEvent,
            inputFilter = inputFilter,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            modifier = textFieldModifier
        )
        POMessageBox(
            text = description,
            modifier = Modifier.padding(top = spacing.space12),
            style = descriptionStyle
        )
    }
}

@Composable
private fun Code(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    style: POField.Style,
    length: Int,
    enabled: Boolean,
    isError: Boolean,
    isFocused: Boolean,
    lifecycleEvent: Lifecycle.Event?,
    inputFilter: POInputFilter?,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier
) {
    val validLength = remember(length) { validLength(length) }
    var values by remember(validLength) { mutableStateOf(values(value.text, validLength, inputFilter)) }
    var focusedIndex by remember(validLength) { mutableIntStateOf(values.focusedIndex()) }
    val clipboardManager = LocalClipboardManager.current
    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Ltr,
        LocalTextToolbar provides ProcessOutTextToolbar(
            view = LocalView.current,
            onPasteRequested = {
                if (clipboardManager.hasText()) {
                    val pastedValues = values(
                        text = clipboardManager.getText()?.text ?: String(),
                        length = validLength,
                        inputFilter = inputFilter
                    )
                    if (!pastedValues.all { it.text.isEmpty() }) {
                        values = pastedValues
                        focusedIndex = values.focusedIndex()
                        onValueChange(values.codeValue())
                    }
                }
            },
            hideUnspecifiedActions = true
        )
    ) {
        var rowWidthPx by remember { mutableIntStateOf(0) }
        val horizontalSpace = spacing.space8
        Row(
            modifier = Modifier
                .focusGroup()
                .fillMaxWidth()
                .onGloballyPositioned { rowWidthPx = it.size.width },
            horizontalArrangement = Arrangement.spacedBy(horizontalSpace),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val focusManager = LocalFocusManager.current
            for (textFieldIndex in values.indices) {
                val focusRequester = remember { FocusRequester() }
                POTextField2(
                    value = values[textFieldIndex],
                    onValueChange = { updatedValue ->
                        if (updatedValue.selection.length == 0) {
                            val currentValue = values[textFieldIndex]
                            val updatedFilteredValue = inputFilter?.filter(updatedValue) ?: updatedValue
                            values = values.mapIndexed { index, textFieldValue ->
                                if (index == textFieldIndex) {
                                    val updatedText = updatedFilteredValue.text.firstOrNull()?.toString() ?: String()
                                    val isTextChanged = textFieldValue.text != updatedText
                                    TextFieldValue(
                                        text = updatedText,
                                        selection = if (isTextChanged) {
                                            TextRange(updatedText.length)
                                        } else {
                                            updatedFilteredValue.selection
                                        }
                                    )
                                } else {
                                    textFieldValue.copy(selection = TextRange.Zero)
                                }
                            }
                            if (textFieldIndex != values.lastIndex &&
                                updatedFilteredValue.text.length == 2 &&
                                updatedFilteredValue.selection.start == 2
                            ) {
                                val nextText = updatedFilteredValue.text.last().toString()
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
                            val isSelectionChangedOnly = currentValue.text == updatedFilteredValue.text &&
                                    currentValue.selection != updatedFilteredValue.selection
                            if (updatedFilteredValue.text.isNotEmpty() &&
                                !isSelectionChangedOnly &&
                                textFieldIndex != values.lastIndex
                            ) {
                                focusedIndex = textFieldIndex + 1
                            }
                            onValueChange(values.codeValue())
                        }
                    },
                    modifier = Modifier.requiredWidth(
                        rememberTextFieldWidth(
                            defaultWidth = dimensions.interactiveComponentMinSize,
                            rowWidth = with(LocalDensity.current) { rowWidthPx.toDp() },
                            space = horizontalSpace,
                            length = validLength
                        )
                    ),
                    textFieldModifier = modifier
                        .onPreviewKeyEvent {
                            if (it.key == Key.Backspace &&
                                it.type == KeyEventType.KeyDown &&
                                textFieldIndex != 0 &&
                                values[textFieldIndex].selection.start == 0
                            ) {
                                values = values.mapIndexed { index, textFieldValue ->
                                    if (index == textFieldIndex - 1) {
                                        TextFieldValue()
                                    } else {
                                        textFieldValue.copy(selection = TextRange.Zero)
                                    }
                                }
                                focusManager.moveFocus(FocusDirection.Previous)
                                onValueChange(values.codeValue())
                            }
                            false
                        }
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (it.isFocused) {
                                focusedIndex = textFieldIndex
                            }
                        },
                    contentPadding = PaddingValues(spacing.space0),
                    fieldStyle = align(style),
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

private fun values(
    text: String,
    length: Int,
    inputFilter: POInputFilter?
): List<TextFieldValue> {
    val values = mutableListOf<TextFieldValue>()
    while (values.size < length) {
        values.add(TextFieldValue())
    }
    val filteredText = inputFilter?.filter(TextFieldValue(text = text))?.text ?: text
    filteredText
        .take(length)
        .forEachIndexed { index, char ->
            val value = char.toString()
            values[index] = TextFieldValue(
                text = value,
                selection = TextRange(value.length)
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

private fun List<TextFieldValue>.codeValue() = TextFieldValue(
    text = joinToString(separator = String()) { it.text }
)
