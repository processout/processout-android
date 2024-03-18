package com.processout.sdk.api.model.response

import com.processout.sdk.api.model.request.POCardTokenizationShouldContinueRequest
import com.processout.sdk.core.ProcessOutResult
import java.util.UUID

/**
 * Defines the response to decide whether the flow should continue or complete after the failure.
 * This response can only be created from [POCardTokenizationShouldContinueRequest.toResponse] function.
 *
 * @param[uuid] Unique identifier of response that must be equal to UUID of request.
 * @param[failure] Failure that occurred. Must be provided from [POCardTokenizationShouldContinueRequest].
 * @param[shouldContinue] Boolean that indicates whether the flow should continue or complete after the [failure].
 */
data class POCardTokenizationShouldContinueResponse internal constructor(
    val uuid: UUID,
    val failure: ProcessOutResult.Failure,
    val shouldContinue: Boolean
)

/**
 * Creates [POCardTokenizationShouldContinueResponse] from [POCardTokenizationShouldContinueRequest].
 *
 * @param[shouldContinue] Boolean that indicates whether the flow should continue or complete after the failure.
 */
fun POCardTokenizationShouldContinueRequest.toResponse(
    shouldContinue: Boolean
) = POCardTokenizationShouldContinueResponse(uuid, failure, shouldContinue)
