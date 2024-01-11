package com.processout.sdk.ui.shared.filter

import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.ui.core.filter.POInputFilter

internal class CardNumberInputFilter : POInputFilter {

    private companion object {
        const val MAX_LENGTH = 19 // Maximum PAN length based on ISO/IEC 7812
    }

    override fun filter(value: TextFieldValue) = value.copy(
        text = value.text.filter { it.isDigit() }.take(MAX_LENGTH)
    )
}
