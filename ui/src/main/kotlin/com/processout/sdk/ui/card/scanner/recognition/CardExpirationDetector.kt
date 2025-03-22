package com.processout.sdk.ui.card.scanner.recognition

import java.util.Calendar

internal class CardExpirationDetector(
    private val calendar: Calendar = Calendar.getInstance()
) : CardAttributeDetector<POScannedCard.Expiration> {

    private val expirationRegex = """(?<!\d)([1-9]|0[1-9]|1[0-2])\s*[\\/.-]\s*(\d{4}|\d{2})(?!\d)""".toRegex()

    override fun firstMatch(candidates: List<String>): POScannedCard.Expiration? {
        val matches = mutableListOf<POScannedCard.Expiration>()
        candidates.reversed().forEach { candidate ->
            expirationRegex.findAll(candidate).forEach { match ->
                val month = match.groupValues[1].toInt()
                val year = normalized(match.groupValues[2].toInt())
                matches.add(
                    POScannedCard.Expiration(
                        month = month,
                        year = year,
                        isExpired = isExpired(month = month, year = year),
                        formatted = formatted(month = month, year = year)
                    )
                )
            }
        }
        return matches.maxWithOrNull(compareBy({ it.year }, { it.month }))
    }

    private fun normalized(year: Int): Int {
        if (year >= 100) {
            return year
        }
        val currentYear = calendar.get(Calendar.YEAR)
        return (currentYear / 100) * 100 + year
    }

    private fun isExpired(month: Int, year: Int): Boolean {
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentYear = calendar.get(Calendar.YEAR)
        return year < currentYear || (year == currentYear && month < currentMonth)
    }

    private fun formatted(month: Int, year: Int): String {
        val formattedMonth = month.toString().padStart(length = 2, padChar = '0')
        val formattedYear = year % 100
        return "$formattedMonth / $formattedYear"
    }
}
