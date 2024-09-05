@file:Suppress("OVERRIDE_DEPRECATION")

package com.processout.sdk.api.dispatcher.card.tokenization

import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.request.POCardTokenizationPreferredSchemeRequest
import com.processout.sdk.api.model.request.POCardTokenizationProcessingRequest
import com.processout.sdk.api.model.request.POCardTokenizationShouldContinueRequest
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.api.model.response.POCardTokenizationPreferredSchemeResponse
import com.processout.sdk.api.model.response.POCardTokenizationShouldContinueResponse
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** @suppress */
@ProcessOutInternalApi
class PODefaultCardTokenizationEventDispatcher : POCardTokenizationEventDispatcher {

    private val _events = MutableSharedFlow<POCardTokenizationEvent>()
    override val events = _events.asSharedFlow()

    private val _preferredSchemeRequest = MutableSharedFlow<POCardTokenizationPreferredSchemeRequest>()
    override val preferredSchemeRequest = _preferredSchemeRequest.asSharedFlow()

    private val _preferredSchemeResponse = MutableSharedFlow<POCardTokenizationPreferredSchemeResponse>()
    val preferredSchemeResponse = _preferredSchemeResponse.asSharedFlow()

    private val _shouldContinueRequest = MutableSharedFlow<POCardTokenizationShouldContinueRequest>()
    override val shouldContinueRequest = _shouldContinueRequest.asSharedFlow()

    private val _shouldContinueResponse = MutableSharedFlow<POCardTokenizationShouldContinueResponse>()
    val shouldContinueResponse = _shouldContinueResponse.asSharedFlow()

    private val _processTokenizedCardRequest = MutableSharedFlow<POCardTokenizationProcessingRequest>()
    override val processTokenizedCardRequest = _processTokenizedCardRequest.asSharedFlow()

    private val _processTokenizedCard = MutableSharedFlow<POCard>()
    override val processTokenizedCard = _processTokenizedCard.asSharedFlow()

    private val _completion = MutableSharedFlow<ProcessOutResult<Any>>()
    val completion = _completion.asSharedFlow()

    // Events

    suspend fun send(event: POCardTokenizationEvent) {
        _events.emit(event)
    }

    // Preferred Scheme

    suspend fun send(request: POCardTokenizationPreferredSchemeRequest) {
        _preferredSchemeRequest.emit(request)
    }

    override suspend fun preferredScheme(response: POCardTokenizationPreferredSchemeResponse) {
        _preferredSchemeResponse.emit(response)
    }

    fun subscribedForPreferredSchemeRequest() = _preferredSchemeRequest.subscriptionCount.value > 0

    // Should Continue

    suspend fun send(request: POCardTokenizationShouldContinueRequest) {
        _shouldContinueRequest.emit(request)
    }

    override suspend fun shouldContinue(response: POCardTokenizationShouldContinueResponse) {
        _shouldContinueResponse.emit(response)
    }

    fun subscribedForShouldContinueRequest() = _shouldContinueRequest.subscriptionCount.value > 0

    // Process Tokenized Card

    suspend fun processTokenizedCardRequest(request: POCardTokenizationProcessingRequest) {
        _processTokenizedCardRequest.emit(request)
    }

    @Deprecated(
        message = "Use replacement function.",
        replaceWith = ReplaceWith("processTokenizedCardRequest(request)")
    )
    suspend fun processTokenizedCard(card: POCard) {
        _processTokenizedCard.emit(card)
    }

    override suspend fun complete(result: ProcessOutResult<Any>) {
        _completion.emit(result)
    }

    fun subscribedForProcessTokenizedCardRequest() = _processTokenizedCardRequest.subscriptionCount.value > 0

    @Deprecated(
        message = "Use replacement function.",
        replaceWith = ReplaceWith("subscribedForProcessTokenizedCardRequest()")
    )
    fun subscribedForProcessTokenizedCard() = _processTokenizedCard.subscriptionCount.value > 0
}
