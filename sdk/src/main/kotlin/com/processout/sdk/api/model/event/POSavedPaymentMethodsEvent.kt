package com.processout.sdk.api.model.event

import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi

/**
 * Defines saved payment methods lifecycle events.
 */
/** @suppress */
@ProcessOutInternalApi
sealed class POSavedPaymentMethodsEvent {

    /**
     * Initial event that is sent prior any other event.
     */
    data object WillStart : POSavedPaymentMethodsEvent()

    /**
     * Event indicates that initialization is complete.
     */
    data object DidStart : POSavedPaymentMethodsEvent()

    /**
     * Event is sent when customer token has been deleted.
     */
    data class DidDeleteCustomerToken(
        val customerId: String,
        val tokenId: String
    ) : POSavedPaymentMethodsEvent()

    /**
     * Event is sent when unretryable error occurs. This is a final event.
     */
    data class DidFail(
        val failure: ProcessOutResult.Failure
    ) : POSavedPaymentMethodsEvent()
}
