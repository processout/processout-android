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
import com.processout.sdk.ui.core.state.POPhoneNumberFieldState

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POLabeledPhoneNumberField(
    state: POPhoneNumberFieldState,
    onRegionCodeChange: (TextFieldValue) -> Unit,
    onNumberChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
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
            onRegionCodeChange = onRegionCodeChange,
            onNumberChange = onNumberChange,
            modifier = modifier,
            textFieldModifier = textFieldModifier,
            fieldStyle = fieldStyle,
            dropdownMenuStyle = dropdownMenuStyle,
            keyboardActions = keyboardActions
        )
    }
}
