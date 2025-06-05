package com.processout.sdk.ui.shared.transformation

import com.processout.sdk.ui.core.transformation.POBaseVisualTransformation

internal class CardExpirationVisualTransformation : POBaseVisualTransformation() {

    private companion object {
        const val SEPARATOR = " / "
        const val DATE_PART_LENGTH = 2
    }

    override fun transform(text: String) = buildString {
        val dateParts = text.chunked(DATE_PART_LENGTH)
        dateParts.getOrNull(0)?.let { month ->
            append(month)
            if (month.length == DATE_PART_LENGTH) {
                append(SEPARATOR)
            }
        }
        dateParts.getOrNull(1)?.let { year ->
            append(year)
        }
    }

    override fun isSeparator(char: Char) = SEPARATOR.contains(char)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
