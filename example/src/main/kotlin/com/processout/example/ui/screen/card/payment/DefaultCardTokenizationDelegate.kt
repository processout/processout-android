package com.processout.example.ui.screen.card.payment

import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.card.tokenization.POCardTokenizationDelegate

class DefaultCardTokenizationDelegate : POCardTokenizationDelegate {

    override fun onEvent(event: POCardTokenizationEvent) {
        POLogger.info("%s", event)
    }
}
