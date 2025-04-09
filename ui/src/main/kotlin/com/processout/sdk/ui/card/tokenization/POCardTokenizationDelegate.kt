package com.processout.sdk.ui.card.tokenization

import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.response.POCardIssuerInformation
import com.processout.sdk.core.ProcessOutResult

/**
 * Delegate that allows to handle events during card tokenization.
 */
interface POCardTokenizationDelegate {

    /**
     * Invoked on card tokenization lifecycle events.
     */
    fun onEvent(event: POCardTokenizationEvent) {}

    /**
     * Allows to choose default preferred card scheme based on issuer information.
     * Primary card scheme is used by default.
     */
    suspend fun preferredScheme(
        issuerInformation: POCardIssuerInformation
    ): String? = issuerInformation.scheme

    /**
     * Allows to decide whether the flow should continue or complete after the failure.
     * Returns _true_ by default.
     */
    suspend fun shouldContinue(
        failure: ProcessOutResult.Failure
    ): Boolean = true
}
