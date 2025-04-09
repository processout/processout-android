package com.processout.sdk.api.model.request

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.response.POCard
import java.util.UUID

/**
 * Request to process tokenized card (authorize invoice or assign customer token).
 *
 * @param[card] Tokenized card.
 * @param[saveCard] Indicates whether the user has chosen to save the card for future payments.
 * @param[uuid] Unique identifier of request.
 */
data class POCardTokenizationProcessingRequest(
    val card: POCard,
    val saveCard: Boolean,
    override val uuid: UUID = UUID.randomUUID()
) : POEventDispatcher.Request
