package com.processout.sdk.ui.core.component.field.code

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
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
    style: POField.Style = POField.default
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(ProcessOutTheme.spacing.small)
    ) {
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
                modifier = modifier.requiredWidth(ProcessOutTheme.dimensions.formComponentHeight),
                style = style,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
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
}
