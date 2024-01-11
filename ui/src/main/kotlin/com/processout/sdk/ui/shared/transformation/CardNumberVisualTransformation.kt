package com.processout.sdk.ui.shared.transformation

internal class CardNumberVisualTransformation : BaseVisualTransformation() {

    private companion object {
        const val PLACEHOLDER_CHAR = '#'
        const val DEFAULT_PATTERN = "#### #### #### #### ###"
    }

    private data class CardNumberFormat(
        val leading: List<IntRange>,
        val patterns: List<String>
    )

    // Reference: https://baymard.com/blog/credit-card-field-auto-format-spaces
    private val formats = listOf(
        CardNumberFormat(
            leading = listOf(34..34, 37..37),
            patterns = listOf("#### ###### #####")
        ),
        CardNumberFormat(
            leading = listOf(62..62),
            patterns = listOf("#### #### #### ####", "###### #############")
        ),
        CardNumberFormat(
            leading = listOf(500000..509999, 560000..589999, 600000..699999),
            patterns = listOf("#### #### #####", "#### ###### #####", "#### #### #### ####", "#### #### #### #### ###")
        ),
        CardNumberFormat(
            leading = listOf(300..305, 309..309, 36..36, 38..39),
            patterns = listOf("#### ###### ####")
        ),
        CardNumberFormat(
            leading = listOf(1..1),
            patterns = listOf("#### ##### ######")
        )
    )

    override fun transform(text: String): String {
        formats.forEach { format ->
            transform(cardNumber = text, format = format)
                ?.let { return it }
        }
        return transform(cardNumber = text, pattern = DEFAULT_PATTERN) ?: text
    }

    private fun transform(cardNumber: String, format: CardNumberFormat): String? {
        format.leading.forEach { leading ->
            // First and last values of the range are expected to be of the same length.
            val leadingLength = leading.first.toString().length
            cardNumber.take(leadingLength).let { cardNumberLeading ->
                if (cardNumberLeading.isNotEmpty() && leading.contains(cardNumberLeading.toInt())) {
                    format.patterns.forEach { pattern ->
                        transform(cardNumber = cardNumber, pattern = pattern)
                            ?.let { return it }
                    }
                }
            }
        }
        return null
    }

    private fun transform(cardNumber: String, pattern: String): String? =
        buildString {
            var cardNumberIndex = 0
            for (index in pattern.indices) {
                if (cardNumberIndex > cardNumber.lastIndex)
                    break
                if (pattern[index] == PLACEHOLDER_CHAR) {
                    append(cardNumber[cardNumberIndex])
                    cardNumberIndex += 1
                } else {
                    append(pattern[index])
                }
            }
            if (cardNumberIndex != cardNumber.length)
                return null
        }

    override fun isSeparator(char: Char) = !char.isDigit()
}
