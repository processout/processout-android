package com.processout.sdk.ui.card.scanner.recognition

internal class CardNumberDetector : CardAttributeDetector<String> {

    private val numberRegex = Regex("(?:\\d\\s*){12,19}")

    override fun firstMatch(candidates: List<String>): String? {
        candidates.forEach { candidate ->
            numberRegex.find(candidate)?.let { match ->
                val number = match.value.filterNot { it.isWhitespace() }
                if (isLuhnChecksumValid(number)) {
                    return number
                }
            }
        }
        return null
    }

    private fun isLuhnChecksumValid(number: String): Boolean {
        val reversedDigits = number.reversed().map { it.digitToInt() }
        val checksum = reversedDigits.mapIndexed { index, digit ->
            if (index % 2 == 1) {
                val doubled = digit * 2
                if (doubled > 9) doubled - 9 else doubled
            } else {
                digit
            }
        }.sum()
        return checksum % 10 == 0
    }
}
