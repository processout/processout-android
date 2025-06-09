package com.processout.sdk.ui.core.component.field.phone

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.dropdown.PODropdownField
import com.processout.sdk.ui.core.component.field.text.POTextField
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
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Row(modifier = modifier) {
        PODropdownField(
            value = state.regionCode,
            onValueChange = { regionCode ->
                onValueChange(regionCode, state.number)
            },
            availableValues = state.regionCodes,
            modifier = Modifier.width(IntrinsicSize.Min),
            fieldContentPadding = PaddingValues(
                start = spacing.large,
                end = 0.dp,
                top = spacing.medium,
                bottom = spacing.medium
            ),
            fieldStyle = fieldStyle,
            menuStyle = dropdownMenuStyle,
            menuMatchesTextFieldWidth = false,
            preferFormattedTextSelection = true,
            isError = state.isError,
            placeholderText = state.regionCodePlaceholder
        )
        val phoneNumberUtil = remember { PhoneNumberUtil.getInstance() }
        POTextField(
            value = state.number,
            onValueChange = { number ->
                if (number.text.startsWith("+")) {
                    try {
                        val parsedNumber = phoneNumberUtil.parse(number.text, null)
                        val parsedRegionCode = phoneNumberUtil.getRegionCodeForCountryCode(parsedNumber.countryCode)
                        var regionCode = state.regionCode
                        if (state.regionCodes.elements.any { it.value == parsedRegionCode }) {
                            regionCode = TextFieldValue(text = parsedRegionCode)
                        }
                        val parsedNationalNumber = parsedNumber.nationalNumber.toString()
                        val nationalNumber = TextFieldValue(
                            text = parsedNationalNumber,
                            selection = TextRange(parsedNationalNumber.length)
                        )
                        val filteredNationalNumber = state.inputFilter?.filter(nationalNumber) ?: nationalNumber
                        onValueChange(regionCode, filteredNationalNumber)
                    } catch (e: NumberParseException) {
                        // ignore
                    }
                } else {
                    val filteredNumber = state.inputFilter?.filter(number) ?: number
                    onValueChange(state.regionCode, filteredNumber)
                }
            },
            modifier = textFieldModifier
                .padding(start = spacing.extraSmall)
                .weight(1f),
            style = fieldStyle,
            enabled = state.enabled,
            isError = state.isError,
            forceTextDirectionLtr = state.forceTextDirectionLtr,
            placeholderText = state.numberPlaceholder,
            visualTransformation = state.visualTransformation ?: VisualTransformation.None,
            keyboardOptions = state.keyboardOptions,
            keyboardActions = keyboardActions
        )
    }
}
