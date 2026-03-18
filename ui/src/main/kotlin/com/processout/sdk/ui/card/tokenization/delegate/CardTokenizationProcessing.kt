package com.processout.sdk.ui.card.tokenization.delegate

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.fold
import java.util.UUID

internal data class CardTokenizationProcessingRequest(
    override val uuid: UUID = UUID.randomUUID(),
    val card: POCard,
    val saveCard: Boolean
) : POEventDispatcher.Request

internal data class CardTokenizationProcessingResponse(
    override val uuid: UUID,
    val result: ProcessOutResult<POCard>
) : POEventDispatcher.Response

internal fun CardTokenizationProcessingRequest.toResponse(
    result: ProcessOutResult<Any>
) = CardTokenizationProcessingResponse(
    uuid,
    result.fold(
        onSuccess = { ProcessOutResult.Success(card) },
        onFailure = { it }
    )
)
