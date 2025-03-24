package com.processout.sdk.ui.card.scanner.recognition

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Scanned card details.
 *
 * @param[number] Formatted card number.
 * @param[expiration] Card expiration details.
 * @param[cardholderName] Cardholder name.
 */
@Parcelize
data class POScannedCard(
    val number: String,
    val expiration: Expiration?,
    val cardholderName: String?
) : Parcelable {

    /**
     * Card expiration details.
     *
     * @param[month] Expiration month.
     * @param[year] Expiration year as a four digits number.
     * @param[isExpired] Indicates whether the expiration date has past, making the card expired.
     * @param[formatted] Formatted month and year.
     */
    @Parcelize
    data class Expiration(
        val month: Int,
        val year: Int,
        val isExpired: Boolean,
        val formatted: String
    ) : Parcelable
}
