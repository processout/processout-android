package com.processout.sdk.ui.core.component.field.phone

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.field.LabeledFieldLayout
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.POFieldLabels
import com.processout.sdk.ui.core.component.field.dropdown.PODropdownField

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POLabeledPhoneNumberField(
    state: POPhoneNumberFieldState,
    onDialingCodeChange: (TextFieldValue) -> Unit,
    onNumberChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    fieldStyle: POField.Style = POField.default,
    dropdownMenuStyle: PODropdownField.MenuStyle = PODropdownField.defaultMenu,
    labelsStyle: POFieldLabels.Style = POFieldLabels.default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    LabeledFieldLayout(
        title = state.title ?: String(),
        description = state.description,
        style = labelsStyle
    ) {
        POPhoneNumberField(
            state = state,
            onDialingCodeChange = onDialingCodeChange,
            onNumberChange = onNumberChange,
            modifier = modifier,
            fieldStyle = fieldStyle,
            dropdownMenuStyle = dropdownMenuStyle,
            keyboardActions = keyboardActions
        )
    }
}
