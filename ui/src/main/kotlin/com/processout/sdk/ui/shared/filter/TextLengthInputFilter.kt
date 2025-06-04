package com.processout.sdk.ui.shared.filter

import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.ui.core.state.POInputFilter

internal class TextLengthInputFilter(
    private val maxLength: Int
) : POInputFilter {

    override fun filter(value: TextFieldValue) = value.copy(
        text = value.text.take(maxLength)
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TextLengthInputFilter
        return maxLength == other.maxLength
    }

    override fun hashCode(): Int {
        return maxLength.hashCode()
    }
}
