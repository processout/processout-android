package com.processout.sdk.api.model.response

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.request.POCardTokenizationPreferredSchemeRequest
import java.util.UUID

/**
 * Defines the response with preferred scheme that will be used for card tokenization by default.
 * This response can only be created from [POCardTokenizationPreferredSchemeRequest.toResponse] function.
 *
 * @param[uuid] Unique identifier of response that must be equal to UUID of request.
 * @param[issuerInformation] Holds information about card issuing institution that issued the card to the card holder.
 * @param[preferredScheme] Preferred scheme that will be used by default for card tokenization.
 */
data class POCardTokenizationPreferredSchemeResponse internal constructor(
    override val uuid: UUID,
    val issuerInformation: POCardIssuerInformation,
    val preferredScheme: String?
) : POEventDispatcher.Response

/**
 * Creates [POCardTokenizationPreferredSchemeResponse] from [POCardTokenizationPreferredSchemeRequest].
 *
 * @param[preferredScheme] Preferred scheme that will be used by default for card tokenization. Will use a primary scheme if _null_.
 */
fun POCardTokenizationPreferredSchemeRequest.toResponse(
    preferredScheme: String?
) = POCardTokenizationPreferredSchemeResponse(uuid, issuerInformation, preferredScheme)
