package com.processout.sdk.ui.core.component.field.phone

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
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
    onRegionCodeChange: (TextFieldValue) -> Unit,
    onNumberChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    fieldStyle: POField.Style = POField.default,
    dropdownMenuStyle: PODropdownField.MenuStyle = PODropdownField.defaultMenu,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Row(modifier = modifier) {
        PODropdownField(
            value = state.regionCode,
            onValueChange = onRegionCodeChange,
            availableValues = state.regionCodes,
            modifier = Modifier.width(IntrinsicSize.Min),
            fieldStyle = fieldStyle,
            menuStyle = dropdownMenuStyle,
            isError = state.isError,
            placeholderText = state.regionCodePlaceholder
        )
        POTextField(
            value = state.number,
            onValueChange = {
                val value = state.inputFilter?.filter(it) ?: it
                onNumberChange(value)
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
