package com.processout.sdk.ui.card.tokenization.component

import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration.*

/**
 * Defines card tokenization view component configuration.
 *
 * @param[cvcRequired] Specifies whether the CVC field should be displayed. Default value is _true_.
 * @param[cardholderNameRequired] Specifies whether the cardholder name field should be displayed. Default value is _true_.
 * @param[cardScanner] Card scanner configuration. Use _null_ to hide, this is a default behaviour.
 * @param[preferredScheme] Preferred scheme selection configuration.
 * Shows scheme selection if co-scheme is available. Use _null_ to hide.
 * @param[billingAddress] Allows to customize the collection of billing address.
 * @param[savingAllowed] Displays checkbox that allows to save the card details for future payments.
 * @param[submitButton] Submit button configuration. Use _null_ to hide.
 * @param[cancelButton] Cancel button configuration. Use _null_ to hide.
 * @param[metadata] Metadata related to the card.
 * @param[style] Allows to customize the look and feel.
 */
data class POCardTokenizationViewComponentConfiguration(
    val cvcRequired: Boolean = true,
    val cardholderNameRequired: Boolean = true,
    val cardScanner: CardScannerConfiguration? = null,
    val preferredScheme: PreferredSchemeConfiguration? = PreferredSchemeConfiguration(),
    val billingAddress: BillingAddressConfiguration = BillingAddressConfiguration(),
    val savingAllowed: Boolean = false,
    val submitButton: Button? = Button(),
    val cancelButton: CancelButton? = CancelButton(),
    val metadata: Map<String, String>? = null,
    val style: Style? = null
)
