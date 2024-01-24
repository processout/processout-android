package com.processout.sdk.ui.shared.transformation

internal class CardExpirationVisualTransformation : BaseVisualTransformation() {

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
}
