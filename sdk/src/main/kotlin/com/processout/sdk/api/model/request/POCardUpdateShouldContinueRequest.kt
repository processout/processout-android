package com.processout.sdk.api.model.request

import com.processout.sdk.core.ProcessOutResult
import java.util.UUID

/**
 * Defines the request to decide whether the flow should continue or complete after the [failure].
 *
 * @param[cardId] Card identifier.
 * @param[failure] Failure that can be inspected to decide whether the flow should continue or complete.
 * @param[uuid] Unique identifier of request.
 */
data class POCardUpdateShouldContinueRequest internal constructor(
    val cardId: String,
    val failure: ProcessOutResult.Failure,
    val uuid: UUID = UUID.randomUUID()
)
