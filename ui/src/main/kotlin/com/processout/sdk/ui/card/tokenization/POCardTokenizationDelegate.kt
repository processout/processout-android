package com.processout.sdk.ui.card.tokenization

import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.response.POCard
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
     * Allows to additionally process tokenized card before completion,
     * for example to authorize an invoice or assign a customer token.
     * Return the result from respective ProcessOut API if it was used.
     * In case of a custom implementation you can pass
     * _ProcessOutResult.Success(Unit)_ or appropriate _ProcessOutResult.Failure()_
     * with _localizedMessage_ that will be shown directly to the user.
     * Failure will be propagated to [shouldContinue] function.
     *
     * @param[card] Tokenized card.
     * @param[saveCard] Indicates whether the user has chosen to save the card for future payments.
     */
    suspend fun processTokenizedCard(
        card: POCard,
        saveCard: Boolean
    ): ProcessOutResult<Any> = ProcessOutResult.Success(Unit)

    /**
     * Allows to choose default preferred card scheme based on issuer information.
     * Primary card scheme is used by default.
     */
    fun preferredScheme(
        issuerInformation: POCardIssuerInformation
    ): String? = issuerInformation.scheme

    /**
     * Allows to decide whether the flow should continue or complete after the failure.
     * Returns _true_ by default.
     */
    fun shouldContinue(
        failure: ProcessOutResult.Failure
    ): Boolean = true
}
