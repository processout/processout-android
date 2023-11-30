package com.processout.sdk.api.dispatcher.card.update

import com.processout.sdk.api.model.event.POCardUpdateEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal object DefaultCardUpdateEventDispatcher : CardUpdateEventDispatcher {

    private val _events = MutableSharedFlow<POCardUpdateEvent>()
    override val events = _events.asSharedFlow()

    override suspend fun send(event: POCardUpdateEvent) {
        _events.emit(event)
    }
}
