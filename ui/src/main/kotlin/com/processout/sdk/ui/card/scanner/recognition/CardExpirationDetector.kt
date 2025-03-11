package com.processout.sdk.ui.card.scanner.recognition

import java.util.Calendar

internal class CardExpirationDetector(
    private val includingExpired: Boolean,
    private val calendar: Calendar = Calendar.getInstance()
) : CardAttributeDetector<POScannedCard.Expiration> {

    private val expirationRegex = """(0?[1-9]|1[0-2])[/\\.-](\d{4}|\d{2})""".toRegex()
    private val separators = listOf('/', '\\', '.', '-')

    override fun firstMatch(candidates: List<String>): POScannedCard.Expiration? {
        candidates.reversed().forEach { candidate ->
            val filtered = candidate.filter { it.isDigit() || separators.contains(it) }
            expirationRegex.find(filtered)?.let { match ->
                val month = match.groupValues[1].toInt()
                val year = normalized(match.groupValues[2].toInt())
                if (!includingExpired && hasExpired(month = month, year = year)) {
                    return null
                }
                return POScannedCard.Expiration(month = month, year = year)
            }
        }
        return null
    }

    private fun hasExpired(month: Int, year: Int): Boolean {
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        return year < currentYear || (year == currentYear && month < currentMonth)
    }

    private fun normalized(year: Int): Int {
        if (year >= 100) {
            return year
        }
        val currentYear = calendar.get(Calendar.YEAR)
        return (currentYear / 100) * 100 + year
    }
}
