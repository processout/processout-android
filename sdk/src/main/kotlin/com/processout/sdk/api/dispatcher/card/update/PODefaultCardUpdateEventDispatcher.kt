package com.processout.sdk.api.dispatcher.card.update

import com.processout.sdk.api.model.event.POCardUpdateEvent
import com.processout.sdk.api.model.request.POCardUpdateShouldContinueRequest
import com.processout.sdk.api.model.response.POCardUpdateShouldContinueResponse
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** @suppress */
@ProcessOutInternalApi
object PODefaultCardUpdateEventDispatcher : POCardUpdateEventDispatcher {

    private val _events = MutableSharedFlow<POCardUpdateEvent>()
    override val events = _events.asSharedFlow()

    private val _shouldContinueRequest = MutableSharedFlow<POCardUpdateShouldContinueRequest>()
    override val shouldContinueRequest = _shouldContinueRequest.asSharedFlow()

    private val _shouldContinueResponse = MutableSharedFlow<POCardUpdateShouldContinueResponse>()
    val shouldContinueResponse = _shouldContinueResponse.asSharedFlow()

    suspend fun send(event: POCardUpdateEvent) {
        _events.emit(event)
    }

    suspend fun send(request: POCardUpdateShouldContinueRequest) {
        _shouldContinueRequest.emit(request)
    }

    override suspend fun shouldContinue(response: POCardUpdateShouldContinueResponse) {
        _shouldContinueResponse.emit(response)
    }

    fun subscribedForShouldContinueRequest() = _shouldContinueRequest.subscriptionCount.value > 0
}
