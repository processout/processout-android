package com.processout.sdk.api.model.response

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.request.POCardTokenizationProcessingRequest
import com.processout.sdk.core.ProcessOutResult
import java.util.UUID

/**
 * Response after processed tokenized card (authorized invoice or assigned customer token).
 *
 * @param[uuid] Unique identifier of response that must be equal to UUID of request.
 * @param[card] Tokenized card.
 * @param[result] Result of tokenized card processing.
 */
data class POCardTokenizationProcessingResponse internal constructor(
    override val uuid: UUID,
    val card: POCard,
    val result: ProcessOutResult<Any>
) : POEventDispatcher.Response

/**
 * Creates [POCardTokenizationProcessingResponse] from [POCardTokenizationProcessingRequest].
 *
 * @param[result] Result of tokenized card processing.
 */
fun POCardTokenizationProcessingRequest.toResponse(
    result: ProcessOutResult<Any>
) = POCardTokenizationProcessingResponse(uuid, card, result)
