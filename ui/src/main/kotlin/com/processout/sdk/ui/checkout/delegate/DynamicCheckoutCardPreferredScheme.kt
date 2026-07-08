package com.processout.sdk.ui.checkout.delegate

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.response.POCardIssuerInformation
import com.processout.sdk.ui.card.tokenization.delegate.CardTokenizationPreferredSchemeRequest
import java.util.UUID

internal data class DynamicCheckoutCardPreferredSchemeResponse(
    override val uuid: UUID,
    val issuerInformation: POCardIssuerInformation,
    val preferredScheme: String?
) : POEventDispatcher.Response

internal fun CardTokenizationPreferredSchemeRequest.toDynamicCheckoutResponse(
    preferredScheme: String?
) = DynamicCheckoutCardPreferredSchemeResponse(uuid, issuerInformation, preferredScheme)
