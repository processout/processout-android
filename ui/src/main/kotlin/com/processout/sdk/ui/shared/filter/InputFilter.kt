package com.processout.sdk.ui.shared.filter

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue

@Immutable
internal interface InputFilter {

    fun filter(value: TextFieldValue): TextFieldValue
}
