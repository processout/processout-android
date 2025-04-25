package com.processout.sdk.ui.shared.provider

import com.processout.sdk.api.model.response.POCardScheme.*
import com.processout.sdk.ui.shared.provider.CardSchemeProvider.IssuerNumbers.*
import com.processout.sdk.ui.shared.provider.CardSchemeProvider.IssuerNumbers.Set
import kotlin.math.pow

internal class CardSchemeProvider {

    private companion object {
        const val IIN_LENGTH = 6
    }

    private data class Issuer(
        val scheme: String,
        val numbers: IssuerNumbers,
        val length: Int
    )

    private sealed interface IssuerNumbers {
        data class Set(val set: kotlin.collections.Set<Int>) : IssuerNumbers
        data class Range(val range: IntRange) : IssuerNumbers
        data class Exact(val value: Int) : IssuerNumbers
    }

    // https://www.bincodes.com/bin-list
    // Sorted by length descending to handle overlapping numbers (like 622126 and 62).
    private val issuers: List<Issuer> = listOf(
        Issuer(scheme = DISCOVER.rawValue, numbers = Range(622126..622925), length = 6),
        Issuer(
            scheme = ELO.rawValue,
            numbers = Set(
                setOf(
                    401178, 401179, 431274, 438935, 451416, 457393, 457631, 457632, 504175, 506699, 506770, 506771,
                    506772, 506773, 506774, 506775, 506776, 506777, 506778, 627780, 636297, 636368, 650031, 650032,
                    650033, 650035, 650036, 650037, 650038, 650039, 650050, 650051, 650405, 650406, 650407, 650408,
                    650409, 650485, 650486, 650487, 650488, 650489, 650530, 650531, 650532, 650533, 650534, 650535,
                    650536, 650537, 650538, 650541, 650542, 650543, 650544, 650545, 650546, 650547, 650548, 650549,
                    650590, 650591, 650592, 650593, 650594, 650595, 650596, 650597, 650598, 650710, 650711, 650712,
                    650713, 650714, 650715, 650716, 650717, 650718, 650720, 650721, 650722, 650723, 650724, 650725,
                    650726, 650727, 650901, 650902, 650903, 650904, 650905, 650906, 650907, 650908, 650909, 650970,
                    650971, 650972, 650973, 650974, 650975, 650976, 650977, 650978, 651652, 651653, 651654, 651655,
                    651656, 651657, 651658, 651659, 655021, 655022, 655023, 655024, 655025, 655026, 655027, 655028,
                    655029, 655050, 655051, 655052, 655053, 655054, 655055, 655056, 655057, 655058
                )
            ),
            length = 6
        ),
        Issuer(
            scheme = ELO.rawValue,
            numbers = Set(
                setOf(
                    50670, 50671, 50672, 50673, 50674, 50675, 50676, 65004, 65041, 65042, 65043, 65049, 65050, 65051,
                    65052, 65055, 65056, 65057, 65058, 65070, 65091, 65092, 65093, 65094, 65095, 65096, 65166, 65167,
                    65500, 65501, 65503, 65504
                )
            ),
            length = 5
        ),
        Issuer(scheme = DISCOVER.rawValue, numbers = Exact(6011), length = 4),
        Issuer(scheme = JCB.rawValue, numbers = Range(3528..3589), length = 4),
        Issuer(scheme = ELO.rawValue, numbers = Exact(509), length = 3),
        Issuer(scheme = DISCOVER.rawValue, numbers = Range(644..649), length = 3),
        Issuer(scheme = DINERS_CLUB_CARTE_BLANCHE.rawValue, numbers = Range(300..305), length = 3),
        Issuer(scheme = DINERS_CLUB_INTERNATIONAL.rawValue, numbers = Exact(309), length = 3),
        Issuer(scheme = MASTERCARD.rawValue, numbers = Range(51..55), length = 2),
        Issuer(scheme = DISCOVER.rawValue, numbers = Exact(65), length = 2),
        Issuer(scheme = UNION_PAY.rawValue, numbers = Exact(62), length = 2),
        Issuer(scheme = AMEX.rawValue, numbers = Set(setOf(34, 37)), length = 2),
        Issuer(scheme = MAESTRO.rawValue, numbers = Set(setOf(50, 56, 57, 58, 59)), length = 2),
        Issuer(scheme = DINERS_CLUB_INTERNATIONAL.rawValue, numbers = Set(setOf(36, 38, 39)), length = 2),
        Issuer(scheme = DINERS_CLUB_UNITED_STATES_AND_CANADA.rawValue, numbers = Range(54..55), length = 2),
        Issuer(scheme = VISA.rawValue, numbers = Exact(4), length = 1),
        Issuer(scheme = MAESTRO.rawValue, numbers = Exact(6), length = 1)
    )

    fun scheme(cardNumber: String): String? {
        val normalized = cardNumber.filter { it.isDigit() }.take(IIN_LENGTH)
        if (normalized.startsWith("0")) {
            return null
        }
        normalized.toIntOrNull()?.let { normalizedInt ->
            val issuer = issuers.find { issuer ->
                val lengthDiff = normalized.length - issuer.length
                if (lengthDiff < 0) return@find false
                // Equalize lookup value with issuer.length
                val value = (normalizedInt / 10f.pow(lengthDiff)).toInt()
                when (val numbers = issuer.numbers) {
                    is Set -> numbers.set.contains(value)
                    is Range -> numbers.range.contains(value)
                    is Exact -> numbers.value == value
                }
            }
            return issuer?.scheme
        }
        return null
    }
}
