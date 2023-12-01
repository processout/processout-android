package com.processout.sdk.api.dispatcher.card.update

import com.processout.sdk.api.model.event.POCardUpdateEvent
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** @suppress */
@ProcessOutInternalApi
object PODefaultCardUpdateEventDispatcher : POCardUpdateEventDispatcher {

    private val _events = MutableSharedFlow<POCardUpdateEvent>()
    override val events = _events.asSharedFlow()

    suspend fun send(event: POCardUpdateEvent) {
        _events.emit(event)
    }
}
