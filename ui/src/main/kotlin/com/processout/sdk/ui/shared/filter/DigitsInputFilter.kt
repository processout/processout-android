package com.processout.sdk.ui.shared.filter

import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.ui.core.state.POInputFilter

internal class DigitsInputFilter(
    private val maxLength: Int? = null
) : POInputFilter {

    override fun filter(value: TextFieldValue) = value.copy(
        text = value.text.filter { it.isDigit() }
            .let { filtered ->
                maxLength?.let { filtered.take(it) } ?: filtered
            }
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DigitsInputFilter
        return maxLength == other.maxLength
    }

    override fun hashCode(): Int {
        return maxLength?.hashCode() ?: javaClass.hashCode()
    }
}
