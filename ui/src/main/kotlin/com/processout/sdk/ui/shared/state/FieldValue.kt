package com.processout.sdk.ui.shared.state

import androidx.compose.ui.text.input.TextFieldValue

internal sealed interface FieldValue {
    data class Text(
        val value: TextFieldValue = TextFieldValue()
    ) : FieldValue

    data class PhoneNumber(
        val regionCode: TextFieldValue = TextFieldValue(),
        val number: TextFieldValue = TextFieldValue()
    ) : FieldValue

    fun isTextEquals(other: FieldValue): Boolean =
        when (this) {
            is Text -> when (other) {
                is Text -> value.text == other.value.text
                else -> false
            }
            is PhoneNumber -> when (other) {
                is PhoneNumber -> regionCode.text == other.regionCode.text &&
                        number.text == other.number.text
                else -> false
            }
        }
}
