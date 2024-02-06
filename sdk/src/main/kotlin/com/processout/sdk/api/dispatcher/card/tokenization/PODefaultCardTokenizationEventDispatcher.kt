package com.processout.sdk.api.dispatcher.card.tokenization

import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.request.POCardTokenizationShouldContinueRequest
import com.processout.sdk.api.model.response.POCardTokenizationShouldContinueResponse
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** @suppress */
@ProcessOutInternalApi
object PODefaultCardTokenizationEventDispatcher : POCardTokenizationEventDispatcher {

    private val _events = MutableSharedFlow<POCardTokenizationEvent>()
    override val events = _events.asSharedFlow()

    private val _shouldContinueRequest = MutableSharedFlow<POCardTokenizationShouldContinueRequest>()
    override val shouldContinueRequest = _shouldContinueRequest.asSharedFlow()

    private val _shouldContinueResponse = MutableSharedFlow<POCardTokenizationShouldContinueResponse>()
    val shouldContinueResponse = _shouldContinueResponse.asSharedFlow()

    suspend fun send(event: POCardTokenizationEvent) {
        _events.emit(event)
    }

    suspend fun send(request: POCardTokenizationShouldContinueRequest) {
        _shouldContinueRequest.emit(request)
    }

    override suspend fun shouldContinue(response: POCardTokenizationShouldContinueResponse) {
        _shouldContinueResponse.emit(response)
    }

    fun subscribedForShouldContinueRequest() = _shouldContinueRequest.subscriptionCount.value > 0
}
