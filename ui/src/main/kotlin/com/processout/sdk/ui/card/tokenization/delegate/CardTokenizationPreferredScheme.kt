package com.processout.sdk.ui.card.tokenization.delegate

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.response.POCardIssuerInformation
import java.util.UUID

internal data class CardTokenizationPreferredSchemeRequest(
    override val uuid: UUID = UUID.randomUUID(),
    val issuerInformation: POCardIssuerInformation
) : POEventDispatcher.Request

internal data class CardTokenizationPreferredSchemeResponse(
    override val uuid: UUID,
    val preferredScheme: String?
) : POEventDispatcher.Response

internal fun CardTokenizationPreferredSchemeRequest.toResponse(
    preferredScheme: String?
) = CardTokenizationPreferredSchemeResponse(uuid, preferredScheme)
