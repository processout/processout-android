package com.processout.sdk.ui.card.tokenization

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class POCardTokenizationFormData(
    internal val cardInformation: CardInformation,
    internal val billingAddress: BillingAddress
) : Parcelable {

    @Parcelize
    data class CardInformation(
        internal val number: String,
        internal val expirationDate: String,
        internal val cardholderName: String
    ) : Parcelable {
        override fun toString() = String()
    }

    @Parcelize
    data class BillingAddress(
        internal val address1: String,
        internal val address2: String,
        internal val city: String,
        internal val state: String,
        internal val postalCode: String,
        internal val countryCode: String
    ) : Parcelable {
        override fun toString() = String()
    }

    override fun toString() = String()
}
