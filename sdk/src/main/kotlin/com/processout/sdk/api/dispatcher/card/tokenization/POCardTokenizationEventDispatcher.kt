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

    /**
     * Notifies whether the flow should continue or complete after the failure.
     * Response must be constructed from request that has been collected by subscribing to [shouldContinueRequest].
     *
     * ```
     * viewModelScope.launch {
     *     with(ProcessOut.instance.dispatchers.cardTokenization) {
     *         shouldContinueRequest.collect { request ->
     *             // Inspect the failure to decide whether the flow should continue or complete.
     *             val shouldContinue = when (val code = request.failure.code) {
     *                 is Generic -> when (code.genericCode) {
     *                     requestInvalidCard,
     *                     cardInvalid -> false
     *                     else -> true
     *                 }
     *                 else -> false
     *             }
     *
     *             // Notify by sending the response which must be constructed from request.
     *             // Note that once you've subscribed to 'shouldContinueRequest'
     *             // it's required to send response back otherwise the card tokenization flow will not proceed.
     *             shouldContinue(request.toResponse(shouldContinue = shouldContinue))
     *         }
     *     }
     * }
     * ```
     */
    suspend fun shouldContinue(response: POCardTokenizationShouldContinueResponse)
}
