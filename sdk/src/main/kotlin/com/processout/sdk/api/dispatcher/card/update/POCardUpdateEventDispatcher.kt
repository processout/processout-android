package com.processout.sdk.api.dispatcher.card.update

import com.processout.sdk.api.model.event.POCardUpdateEvent
import kotlinx.coroutines.flow.SharedFlow

/**
 * Dispatcher that allows to handle events during card updates.
 */
interface POCardUpdateEventDispatcher {

    /** Allows to subscribe for card update lifecycle events. */
    val events: SharedFlow<POCardUpdateEvent>
}
