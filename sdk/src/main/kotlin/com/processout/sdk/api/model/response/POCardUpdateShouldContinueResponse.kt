package com.processout.sdk.api.model.response

import com.processout.sdk.api.model.request.POCardUpdateShouldContinueRequest
import java.util.UUID

/**
 * Defines the response to decide whether the flow should continue or complete after the failure.
 * This response can only be created from [POCardUpdateShouldContinueRequest.toResponse] to use the same UUID.
 *
 * @param[uuid] Unique identifier of response that must be equal to UUID of request.
 * @param[shouldContinue] Boolean that indicates whether the flow should continue or complete after the failure.
 */
data class POCardUpdateShouldContinueResponse internal constructor(
    val uuid: UUID,
    val shouldContinue: Boolean
)

/**
 * Creates response from request to use the same UUID.
 *
 * @param[shouldContinue] Boolean that indicates whether the flow should continue or complete after the failure.
 */
fun POCardUpdateShouldContinueRequest.toResponse(
    shouldContinue: Boolean
) = POCardUpdateShouldContinueResponse(uuid, shouldContinue)
