package com.processout.sdk.api.dispatcher.card.tokenization

import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.request.POCardTokenizationPreferredSchemeRequest
import com.processout.sdk.api.model.request.POCardTokenizationShouldContinueRequest
import com.processout.sdk.api.model.response.POCardTokenizationPreferredSchemeResponse
import com.processout.sdk.api.model.response.POCardTokenizationShouldContinueResponse
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.coroutines.flow.SharedFlow

/**
 * Dispatcher that allows to handle events during card tokenization.
 */
/** @suppress */
@ProcessOutInternalApi
interface POCardTokenizationEventDispatcher {

    /** Allows to subscribe for card tokenization lifecycle events. */
    val events: SharedFlow<POCardTokenizationEvent>

    val preferredSchemeRequest: SharedFlow<POCardTokenizationPreferredSchemeRequest>

    /**
     * Allows to subscribe for request to decide whether the flow should continue or complete after the failure.
     * Once you've subscribed it's required to call [shouldContinue] for each request to proceed with the card tokenization flow.
     */
    val shouldContinueRequest: SharedFlow<POCardTokenizationShouldContinueRequest>

    suspend fun preferredScheme(response: POCardTokenizationPreferredSchemeResponse)

    suspend fun shouldContinue(response: POCardTokenizationShouldContinueResponse)
}
