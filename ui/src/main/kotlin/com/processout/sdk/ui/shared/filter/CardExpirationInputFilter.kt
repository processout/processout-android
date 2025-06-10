@file:Suppress("RegExpSimplifiable")

package com.processout.sdk.ui.shared.filter

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.ui.core.state.POInputFilter
import kotlin.math.max

internal class CardExpirationInputFilter : POInputFilter {

    private companion object {
        val patternRegex = "^(0+$|0+[1-9]|1[0-2]{0,1}|[2-9])([0-9]{0,2})".toRegex()
        const val MONTH_LENGTH = 2
    }

    private data class Expiration(
        val month: String,
        val year: String
    )

    override fun filter(value: TextFieldValue): TextFieldValue {
        val expiration = expiration(value.text)
        if (expiration.month.isEmpty()) {
            return TextFieldValue()
        }
        val formatted = formatted(month = expiration.month, year = expiration.year)
        val selection = if (value.selection.length == 0 && value.selection.end == value.text.length)
            TextRange(index = formatted.length) else value.selection
        return value.copy(text = formatted, selection = selection)
    }

    private fun expiration(text: String): Expiration {
        val normalized = text.filter { it.isDigit() }
        if (normalized.isEmpty()) {
            return Expiration(month = String(), year = String())
        }
        patternRegex.find(normalized)?.let { match ->
            val month = match.groupValues[1]
            val year = match.groupValues[2]
            return Expiration(month = month, year = year)
        }
        return Expiration(month = String(), year = String())
    }

    private fun formatted(month: String, year: String) = buildString {
        append(formatted(month = month, forcePadding = year.isNotEmpty()))
        append(year)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
