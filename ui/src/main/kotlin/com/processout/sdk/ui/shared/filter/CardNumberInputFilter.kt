package com.processout.sdk.ui.shared.filter

import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.ui.core.state.POInputFilter

internal class CardNumberInputFilter : POInputFilter {

    private companion object {
        const val MAX_LENGTH = 19 // Maximum PAN length based on ISO/IEC 7812
    }

    override fun filter(value: TextFieldValue) = value.copy(
        text = value.text.filter { it.isDigit() }.take(MAX_LENGTH)
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
