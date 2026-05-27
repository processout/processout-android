package com.processout.sdk.ui.checkout.delegate

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.response.POCardIssuerInformation
import com.processout.sdk.ui.card.tokenization.delegate.CardTokenizationEligibilityRequest
import com.processout.sdk.ui.card.tokenization.delegate.POCardTokenizationEligibility
import java.util.UUID

internal data class DynamicCheckoutCardEligibilityResponse(
    override val uuid: UUID,
    val iin: String,
    val issuerInformation: POCardIssuerInformation,
    val eligibility: POCardTokenizationEligibility?
) : POEventDispatcher.Response

internal fun CardTokenizationEligibilityRequest.toDynamicCheckoutResponse(
    eligibility: POCardTokenizationEligibility?
) = DynamicCheckoutCardEligibilityResponse(uuid, iin, issuerInformation, eligibility)
