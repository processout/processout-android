package com.processout.sdk.ui.card.scanner.recognition

import java.text.Normalizer

internal class CardholderNameDetector : CardAttributeDetector<String> {

    private val delimiterRegex = Regex("\\W+")
    private val diacriticsRegex = Regex("\\p{InCombiningDiacriticalMarks}+")

    override fun firstMatch(candidates: List<String>): String? =
        candidates.reversed().find { candidate ->
            candidate
                .stripDiacritics()
                .uppercase()
                .split(delimiterRegex)
                .forEach { word ->
                    if (word.any { it.isDigit() || it == '_' } ||
                        restrictedWords.contains(word)) {
                        return@find false
                    }
                }
            true
        }

    private fun String.stripDiacritics(): String {
        val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
        return diacriticsRegex.replace(normalized, String())
    }

    private val restrictedWords = setOf(
        // Card networks
        "VISA",
        "MASTERCARD",
        "AMEX",
        "AMERICAN",
        "EXPRESS",
        "DISCOVER",
        "DINERS",
        "CLUB",
        "UNION",
        "PAY",
        "JCB",
        "NETWORK",
        "INTERNATIONAL",
        "CARD",
        "MEMBER",
        "SECURE",
        "CREDIT",
        "DEBIT",
        "CHIP",
        "NFC",

        // Card labels
        "PLATINUM",
        "GOLD",
        "SILVER",
        "TITANIUM",
        "BUSINESS",
        "CORPORATE",
        "REWARD",
        "SECURED",
        "ADVANCE",
        "WORLD",
        "ELITE",
        "PREFERRED",
        "INFINITE",
        "SELECT",
        "PRIVILEGE",
        "PREMIER",
        "PLUS",
        "EDGE",
        "ULTIMATE",
        "SIGNATURE",

        // Issuing banks (generic terms and widely used names)
        "BANK",
        "CHASE",
        "CITI",
        "WELLS",
        "FARGO",
        "CAPITAL",
        "HSBC",
        "BARCLAYS",
        "SANTANDER",
        "BBVA",
        "NATWEST",
        "RBC",
        "TD",
        "SCOTIABANK",
        "BMO",
        "SOCIETE",
        "GENERALE",
        "STANDARD",
        "CHARTERED",
        "DEUTSCHE",

        // Contact information
        "ADDRESS",
        "STREET",
        "ROAD",
        "AVENUE",
        "CITY",
        "STATE",
        "ZIP",
        "COUNTRY",
        "TELEPHONE",
        "EMAIL",

        // Security features and miscellaneous text
        "VALID",
        "THRU",
        "EXPIRY",
        "DATE",
        "EXPIRES",
        "FROM",
        "UNTIL",
        "AUTHORIZED",
        "USER",
        "USE",
        "ONLY",
        "AUTHORIZATION",
        "SIGNATURE",
        "LINE",
        "VOID",
        "MAGNETIC",
        "STRIPE",
        "NUMBER",
        "CODE",
        "SECURE",

        // Generic terms
        "CONTACT",
        "SERVICE",
        "CUSTOMER",
        "SUPPORT",
        "WEBSITE",
        "HOTLINE",
        "HELP",
        "TERMS",
        "CONDITIONS",
        "LIMITATIONS"
    )
}
