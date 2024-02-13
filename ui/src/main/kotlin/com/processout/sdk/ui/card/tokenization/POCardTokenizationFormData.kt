package com.processout.sdk.ui.card.tokenization

import android.os.Parcelable
import com.processout.sdk.api.model.response.POCardIssuerInformation
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.parcelize.Parcelize

/** @suppress */
@ProcessOutInternalApi
@Parcelize
class POCardTokenizationFormData internal constructor(
    internal val cardInformation: CardInformation,
    internal val billingAddress: BillingAddress? = null
) : Parcelable {

    @Parcelize
    class CardInformation internal constructor(
        internal val number: String,
        internal val expiration: String,
        internal val cvc: String,
        internal val cardholderName: String,
        internal val issuerInformation: POCardIssuerInformation?,
        internal val preferredScheme: String?
    ) : Parcelable {
        override fun toString() = String()
    }

    @Parcelize
    class BillingAddress internal constructor(
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
