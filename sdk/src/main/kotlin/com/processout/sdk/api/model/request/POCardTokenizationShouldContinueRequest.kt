package com.processout.sdk.api.model.request

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import java.util.UUID

/**
 * Defines the request to decide whether the flow should continue or complete after the [failure].
 *
 * @param[failure] Failure that can be inspected to decide whether the flow should continue or complete.
 * @param[uuid] Unique identifier of request.
 */
data class POCardTokenizationShouldContinueRequest @ProcessOutInternalApi constructor(
    val failure: ProcessOutResult.Failure,
    override val uuid: UUID = UUID.randomUUID()
) : POEventDispatcher.Request
