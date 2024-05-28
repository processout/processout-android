package com.processout.sdk.ui.shared.filter

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.ui.core.filter.POInputFilter

internal class PhoneNumberInputFilter : POInputFilter {

    override fun filter(value: TextFieldValue): TextFieldValue {
        var filtered = value.text.filter { it.isDigit() }
        filtered = "+$filtered"
        val lengthDiff = value.text.length - filtered.length
        val selection = with(value) {
            if (selection.length == 0) {
                if (selection.start == text.length) {
                    TextRange(filtered.length)
                } else if (selection.start == 0) {
                    TextRange(selection.start + 1)
                } else if (lengthDiff > 0) {
                    TextRange(selection.start - lengthDiff)
                } else {
                    selection
                }
            } else {
                selection
            }
        }
        return value.copy(text = filtered, selection = selection)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
