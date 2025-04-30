package com.processout.sdk.api.model.response

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.request.POCardUpdateShouldContinueRequest
import com.processout.sdk.core.ProcessOutResult
import java.util.UUID

/**
 * Defines the response to decide whether the flow should continue or complete after the failure.
 * This response can only be created from [POCardUpdateShouldContinueRequest.toResponse].
 *
 * @param[uuid] Unique identifier of response that must be equal to UUID of request.
 * @param[failure] Failure that occurred. Must be provided from [POCardUpdateShouldContinueRequest].
 * @param[shouldContinue] Boolean that indicates whether the flow should continue or complete after the [failure].
 */
data class POCardUpdateShouldContinueResponse internal constructor(
    override val uuid: UUID,
    val failure: ProcessOutResult.Failure,
    val shouldContinue: Boolean
) : POEventDispatcher.Response

/**
 * Creates [POCardUpdateShouldContinueResponse] from [POCardUpdateShouldContinueRequest].
 *
 * @param[shouldContinue] Boolean that indicates whether the flow should continue or complete after the failure.
 */
fun POCardUpdateShouldContinueRequest.toResponse(
    shouldContinue: Boolean
) = POCardUpdateShouldContinueResponse(uuid, failure, shouldContinue)
