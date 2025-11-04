package com.processout.sdk.ui.core.component.field.phone

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POMessageBox
import com.processout.sdk.ui.core.component.PORequestFocus
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.dropdown.PODropdownField
import com.processout.sdk.ui.core.component.field.text.POTextField2
import com.processout.sdk.ui.core.state.POPhoneNumberFieldState
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POPhoneNumberField(
    state: POPhoneNumberFieldState,
    onValueChange: (TextFieldValue, TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    fieldStyle: POField.Style = POField.default,
    dropdownMenuStyle: PODropdownField.MenuStyle = PODropdownField.defaultMenu,
    descriptionStyle: POMessageBox.Style = POMessageBox.error2,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            var requestFocus by remember { mutableStateOf(false) }
            PODropdownField(
                value = state.regionCode,
                onValueChange = { regionCode ->
                    requestFocus = true
                    onValueChange(regionCode, state.number)
                },
                availableValues = state.regionCodes,
                modifier = Modifier.width(IntrinsicSize.Min),
                contentPadding = PaddingValues(
                    start = spacing.space12,
                    end = spacing.space0,
                    top = spacing.space6,
                    bottom = spacing.space6
                ),
                fieldStyle = fieldStyle,
                menuStyle = dropdownMenuStyle,
                menuMatchesTextFieldWidth = false,
                preferFormattedTextSelection = true,
                isError = state.isError,
                label = state.regionCodeLabel
            )
            val focusRequester = remember { FocusRequester() }
            val phoneNumberUtil = remember { PhoneNumberUtil.getInstance() }
            POTextField2(
                value = state.number,
                onValueChange = { number ->
                    if (number.text.startsWith('+')) {
                        try {
                            val filteredNumber = number.text.filterIndexed { index, char ->
                                (index == 0 && char == '+') || char.isDigit()
                            }
                            val parsedNumber = phoneNumberUtil.parse(filteredNumber, null)
                            val parsedRegionCode = phoneNumberUtil.getRegionCodeForNumber(parsedNumber) ?: String()
                            var regionCode = state.regionCode
                            if (state.regionCodes.elements.any { it.value == parsedRegionCode }) {
                                regionCode = TextFieldValue(text = parsedRegionCode)
                            }
                            val parsedNationalNumber = parsedNumber.nationalNumber.toString()
                            val nationalNumber = TextFieldValue(
                                text = parsedNationalNumber,
                                selection = TextRange(parsedNationalNumber.length)
                            )
                            onValueChange(regionCode, nationalNumber)
                        } catch (e: NumberParseException) {
                            // ignore
                        }
                    } else {
                        val filteredNumber = state.inputFilter?.filter(number) ?: number
                        onValueChange(state.regionCode, filteredNumber)
                    }
                },
                modifier = Modifier
                    .padding(start = spacing.space4)
                    .weight(1f),
                textFieldModifier = textFieldModifier.focusRequester(focusRequester),
                fieldStyle = fieldStyle,
                enabled = state.enabled,
                isError = state.isError,
                forceTextDirectionLtr = state.forceTextDirectionLtr,
                label = state.numberLabel,
                visualTransformation = state.visualTransformation ?: VisualTransformation.None,
                keyboardOptions = state.keyboardOptions,
                keyboardActions = keyboardActions
            )
            if (requestFocus) {
                requestFocus = false
                PORequestFocus(focusRequester)
            }
        }
        POMessageBox(
            text = state.description,
            modifier = Modifier.padding(top = spacing.space12),
            style = descriptionStyle
        )
    }
}
