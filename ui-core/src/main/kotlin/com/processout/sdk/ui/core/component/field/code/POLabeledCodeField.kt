package com.processout.sdk.ui.core.component.field.code

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.Lifecycle
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.field.LabeledFieldLayout
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.POFieldLabels
import com.processout.sdk.ui.core.state.POInputFilter

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POLabeledCodeField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    title: String,
    description: String?,
    modifier: Modifier = Modifier,
    fieldStyle: POField.Style = POCodeField.default,
    labelsStyle: POFieldLabels.Style = POFieldLabels.default,
    length: Int = POCodeField.LengthMax,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    enabled: Boolean = true,
    isError: Boolean = false,
    isFocused: Boolean = false,
    lifecycleEvent: Lifecycle.Event? = null,
    inputFilter: POInputFilter? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    LabeledFieldLayout(
        title = title,
        description = description,
        style = labelsStyle,
        horizontalAlignment = horizontalAlignment
    ) {
        POCodeField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            style = fieldStyle,
            length = length,
            horizontalAlignment = horizontalAlignment,
            enabled = enabled,
            isError = isError,
            isFocused = isFocused,
            lifecycleEvent = lifecycleEvent,
            inputFilter = inputFilter,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions
        )
    }
}
