package com.processout.sdk.ui.shared.formatter

import com.processout.sdk.ui.core.formatter.POFormatter
import kotlin.math.max

internal class CardExpirationFormatter : POFormatter {

    private companion object {
        val patternRegex = "^(0+$|0+[1-9]|1[0-2]{0,1}|[2-9])([0-9]{0,2})".toRegex()
        const val SEPARATOR = " / "
        const val MONTH_LENGTH = 2
    }

    private data class Expiration(
        val month: String,
        val year: String
    )

    override fun format(string: String): String {
        val expiration = expiration(string)
        if (expiration.month.isEmpty()) {
            return String()
        }
        return formatted(month = expiration.month, year = expiration.year)
    }

    private fun formatted(month: String, year: String): String {
        val formattedMonth = formatted(month = month, forcePadding = year.isNotEmpty())
        return buildString {
            append(formattedMonth)
            if (formattedMonth.length == MONTH_LENGTH) {
                append(SEPARATOR)
            }
            append(year)
        }
    }

    private fun formatted(month: String, forcePadding: Boolean): String {
        month.toIntOrNull()?.let { monthValue ->
            if (monthValue == 0) {
                return "0"
            }
            val monthValueString = monthValue.toString()
            val isPadded = month.first() == '0'
            if (!(forcePadding || isPadded || (2..9).contains(monthValue))) {
                return monthValueString
            }
            val paddingLength = max(MONTH_LENGTH - monthValueString.length, 0)
            return buildString {
                append("0".repeat(paddingLength))
                append(monthValueString)
            }
        }
        return month
    }

    private fun expiration(string: String): Expiration {
        val normalizedString = string.filter { it.isDigit() }
        if (normalizedString.isEmpty()) {
            return Expiration(month = String(), year = String())
        }
        patternRegex.find(normalizedString)?.let { match ->
            val month = match.groupValues[1]
            val year = match.groupValues[2]
            return Expiration(month = month, year = year)
        }
        return Expiration(month = String(), year = String())
    }
}
