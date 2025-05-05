package com.processout.sdk.ui.card.tokenization.delegate

import com.processout.sdk.api.model.response.POCardScheme
import com.processout.sdk.core.ProcessOutResult

/**
 * Represents card eligibility for tokenization.
 */
sealed class POCardTokenizationEligibility {

    /**
     * Indicates that the card is eligible for tokenization, optionally restricted to the specific card scheme.
     */
    data class Eligible(
        val scheme: POCardScheme? = null
    ) : POCardTokenizationEligibility()

    /**
     * Indicates that the card is not eligible for tokenization.
     * You may provide a failure with the [ProcessOutResult.Failure.localizedMessage] that will be shown directly to the user.
     */
    data class NotEligible(
        val failure: ProcessOutResult.Failure? = null
    ) : POCardTokenizationEligibility()
}
