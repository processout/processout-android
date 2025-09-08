package com.processout.sdk.ui.card.tokenization.delegate

import com.processout.sdk.api.model.response.POCardIssuerInformation

/**
 * Card tokenization state.
 *
 * @param[iin] Issuer identification number.
 * @param[issuerInformation] Resolved issuer information.
 * @param[eligibility] Indicates whether the card is eligible for tokenization.
 * @param[preferredScheme] Preferred scheme.
 * @param[countryCode] Country code.
 * @param[submitAllowed] Indicates whether submitting the form is allowed
 * (i.e., all entered fields are valid and the card is eligible for tokenization).
 * @param[submitting] Indicates whether the form is currently being submitted.
 */
data class POCardTokenizationState(
    val iin: String?,
    val issuerInformation: POCardIssuerInformation?,
    val eligibility: POCardTokenizationEligibility,
    val preferredScheme: String?,
    val countryCode: String?,
    val submitAllowed: Boolean,
    val submitting: Boolean
)
