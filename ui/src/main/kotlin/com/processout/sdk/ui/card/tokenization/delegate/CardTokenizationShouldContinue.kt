package com.processout.sdk.ui.card.tokenization.delegate

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.core.ProcessOutResult
import java.util.UUID

internal data class CardTokenizationShouldContinueRequest(
    override val uuid: UUID = UUID.randomUUID(),
    val failure: ProcessOutResult.Failure
) : POEventDispatcher.Request

internal data class CardTokenizationShouldContinueResponse(
    override val uuid: UUID,
    val failure: ProcessOutResult.Failure,
    val shouldContinue: Boolean
) : POEventDispatcher.Response

internal fun CardTokenizationShouldContinueRequest.toResponse(
    shouldContinue: Boolean
) = CardTokenizationShouldContinueResponse(uuid, failure, shouldContinue)
