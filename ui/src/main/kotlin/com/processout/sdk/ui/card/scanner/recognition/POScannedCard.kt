package com.processout.sdk.ui.card.scanner.recognition

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class POScannedCard(
    val number: String,
    val expiration: Expiration?,
    val cardholderName: String?
) : Parcelable {

    @Parcelize
    data class Expiration(
        val month: Int,
        val year: Int
    ) : Parcelable
}
