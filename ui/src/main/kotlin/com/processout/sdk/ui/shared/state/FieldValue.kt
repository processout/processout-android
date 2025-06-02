package com.processout.sdk.ui.shared.state

import androidx.compose.ui.text.input.TextFieldValue

internal sealed interface FieldValue {
    data class Text(
        val value: TextFieldValue
    ) : FieldValue

    data class PhoneNumber(
        val dialingCode: TextFieldValue,
        val number: TextFieldValue
    ) : FieldValue
}
