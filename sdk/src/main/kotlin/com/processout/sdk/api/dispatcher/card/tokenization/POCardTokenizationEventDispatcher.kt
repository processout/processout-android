package com.processout.sdk.api.dispatcher.card.tokenization

import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.request.POCardTokenizationPreferredSchemeRequest
import com.processout.sdk.api.model.request.POCardTokenizationProcessingRequest
import com.processout.sdk.api.model.request.POCardTokenizationShouldContinueRequest
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.api.model.response.POCardTokenizationPreferredSchemeResponse
import com.processout.sdk.api.model.response.POCardTokenizationShouldContinueResponse
import com.processout.sdk.core.ProcessOutResult
import kotlinx.coroutines.flow.SharedFlow

/**
 * Dispatcher that allows to handle events during card tokenization.
 */
@Deprecated(message = "Use API with POCardTokenizationDelegate instead.")
interface POCardTokenizationEventDispatcher {

    /** Allows to subscribe for card tokenization lifecycle events. */
    val events: SharedFlow<POCardTokenizationEvent>

    /**
     * Allows to subscribe for request to choose a preferred scheme that will be used for card tokenization by default.
     * Once you've subscribed it's required to call [preferredScheme] for each request.
     */
    val preferredSchemeRequest: SharedFlow<POCardTokenizationPreferredSchemeRequest>

    /**
     * Allows to subscribe for request to decide whether the flow should continue or complete after the failure.
     * Once you've subscribed it's required to call [shouldContinue] for each request to proceed with the card tokenization flow.
     */
    val shouldContinueRequest: SharedFlow<POCardTokenizationShouldContinueRequest>

    /**
     * Subscribe to additionally process tokenized card before completion.
     * For example to authorize an invoice or assign customer token.
     * Once you've subscribed it's required to call [complete] after processing.
     */
    val processTokenizedCardRequest: SharedFlow<POCardTokenizationProcessingRequest>

    /**
     * Subscribe to additionally process tokenized card before completion.
     * For example to authorize an invoice or assign customer token.
     * Once you've subscribed it's required to call [complete] after processing.
     */
    @Deprecated(
        message = "Use replacement flow.",
        replaceWith = ReplaceWith("processTokenizedCardRequest")
    )
    val processTokenizedCard: SharedFlow<POCard>

    /**
     * Allows to provide a preferred scheme that will be used for card tokenization by default.
     * Response must be constructed from request that has been collected by subscribing to [preferredSchemeRequest].
     *
     * ```
     * viewModelScope.launch {
     *     with(ProcessOut.instance.dispatchers.cardTokenization) {
     *         preferredSchemeRequest.collect { request ->
     *             // Inspect issuer information to choose a default preferred scheme.
     *             val preferredScheme = when (request.issuerInformation.scheme) {
     *                 "visa", "mastercard" -> request.issuerInformation.coScheme
     *                 else -> request.issuerInformation.scheme
     *             }
     *             // Send the response with preferred scheme which must be constructed from request.
     *             // Note that once you've subscribed to 'preferredSchemeRequest' it's required to send response back.
     *             // Implementation will use primary scheme if 'preferredScheme' is null.
     *             preferredScheme(request.toResponse(preferredScheme = preferredScheme))
     *         }
     *     }
     * }
     * ```
     */
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

    /**
     * Notify that processing of tokenized card is complete (e.g. after invoice authorization or assigning of a customer token).
     * Pass the result from respective ProcessOut API. If processing is done by a custom implementation you can pass
     * _ProcessOutResult.Success(Unit)_ or appropriate _ProcessOutResult.Failure(...)_.
     * Calling this method will complete the flow, unless you've subscribed to [shouldContinueRequest] to decide it on your own in case of failure.
     */
    suspend fun complete(result: ProcessOutResult<Any>)
}
