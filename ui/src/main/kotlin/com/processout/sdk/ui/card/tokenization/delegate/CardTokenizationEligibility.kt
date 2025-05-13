package com.processout.sdk.ui.card.tokenization.delegate

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.response.POCardIssuerInformation
import java.util.UUID

internal data class CardTokenizationEligibilityRequest(
    override val uuid: UUID = UUID.randomUUID(),
    val iin: String,
    val issuerInformation: POCardIssuerInformation
) : POEventDispatcher.Request

internal data class CardTokenizationEligibilityResponse(
    override val uuid: UUID,
    val eligibility: POCardTokenizationEligibility
) : POEventDispatcher.Response

internal fun CardTokenizationEligibilityRequest.toResponse(
    eligibility: POCardTokenizationEligibility
) = CardTokenizationEligibilityResponse(uuid, eligibility)
