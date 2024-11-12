package com.processout.sdk.ui.checkout.screen.field

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent.FieldValueChanged
import com.processout.sdk.ui.core.component.field.checkbox.POCheckbox
import com.processout.sdk.ui.shared.state.FieldState

@Composable
internal fun CheckboxField(
    id: String,
    state: FieldState,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: POCheckbox.Style,
    modifier: Modifier = Modifier
) {
    POCheckbox(
        text = state.title ?: String(),
        checked = state.value.text.toBooleanStrictOrNull() ?: false,
        onCheckedChange = {
            onEvent(
                FieldValueChanged(
                    paymentMethodId = id,
                    fieldId = state.id,
                    value = TextFieldValue(text = it.toString())
                )
            )
        },
        modifier = modifier,
        style = style,
        enabled = state.enabled,
        isError = state.isError
    )
}
