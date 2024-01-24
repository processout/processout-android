package com.processout.sdk.api.dispatcher.card.update

import com.processout.sdk.api.model.event.POCardUpdateEvent
import com.processout.sdk.api.model.request.POCardUpdateShouldContinueRequest
import com.processout.sdk.api.model.response.POCardUpdateShouldContinueResponse
import kotlinx.coroutines.flow.SharedFlow

/**
 * Dispatcher that allows to handle events during card updates.
 */
interface POCardUpdateEventDispatcher {

    /** Allows to subscribe for card update lifecycle events. */
    val events: SharedFlow<POCardUpdateEvent>

    /**
     * Allows to subscribe for request to decide whether the flow should continue or complete after the failure.
     * Once you've subscribed it's required to call [shouldContinue] for each request to proceed with the card update flow.
     */
    val shouldContinueRequest: SharedFlow<POCardUpdateShouldContinueRequest>

    /**
     * Notifies whether the flow should continue or complete after the failure.
     * Response must be constructed from request that has been collected by subscribing to [shouldContinueRequest].
     *
     * ```
     * viewModelScope.launch {
     *     with(ProcessOut.instance.dispatchers.cardUpdate) {
     *         shouldContinueRequest.collect { request ->
     *             // Inspect the failure to decide whether the flow should continue or complete.
     *             val shouldContinue = when (val code = request.failure.code) {
     *                 is Generic -> when (code.genericCode) {
     *                     requestInvalidCard,
     *                     cardInvalid,
     *                     cardBadTrackData,
     *                     cardMissingCvc,
     *                     cardInvalidCvc,
     *                     cardFailedCvc,
     *                     cardFailedCvcAndAvs -> true
     *                     else -> false
     *                 }
     *                 else -> false
     *             }
     *
     *             // Notify by sending the response which must be constructed from request.
     *             // Note that once you've subscribed to 'shouldContinueRequest'
     *             // it's required to send response back otherwise the card update flow will not proceed.
     *             shouldContinue(request.toResponse(shouldContinue = shouldContinue))
     *         }
     *     }
     * }
     * ```
     */
    suspend fun shouldContinue(response: POCardUpdateShouldContinueResponse)
}
