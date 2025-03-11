package com.processout.sdk.ui.card.scanner.recognition

internal class CardholderNameDetector : CardAttributeDetector<String> {

    override fun firstMatch(candidates: List<String>): String? {
        // TODO
        return null
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
