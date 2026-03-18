package com.processout.sdk.ui.card.update.delegate

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.core.ProcessOutResult
import java.util.UUID

internal data class CardUpdateShouldContinueRequest(
    override val uuid: UUID = UUID.randomUUID(),
    val cardId: String,
    val failure: ProcessOutResult.Failure
) : POEventDispatcher.Request

internal data class CardUpdateShouldContinueResponse(
    override val uuid: UUID,
    val failure: ProcessOutResult.Failure,
    val shouldContinue: Boolean
) : POEventDispatcher.Response

internal fun CardUpdateShouldContinueRequest.toResponse(
    shouldContinue: Boolean
) = CardUpdateShouldContinueResponse(uuid, failure, shouldContinue)
