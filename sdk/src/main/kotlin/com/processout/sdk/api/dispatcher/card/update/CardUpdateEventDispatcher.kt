package com.processout.sdk.api.dispatcher.card.update

import com.processout.sdk.api.model.event.POCardUpdateEvent

internal interface CardUpdateEventDispatcher : POCardUpdateEventDispatcher {

    suspend fun send(event: POCardUpdateEvent)
}
