package com.processout.sdk.ui.card.tokenization

internal data class CardTokenizationFormData(
    val cardInformation: CardInformation,
    val billingAddress: BillingAddress
) {
    data class CardInformation(
        val number: String,
        val expiration: String,
        val cvc: String,
        val cardholderName: String,
        val preferredScheme: String?
    )

    data class BillingAddress(
        val countryCode: String,
        val address1: String,
        val address2: String,
        val city: String,
        val state: String,
        val postalCode: String
    )
}
