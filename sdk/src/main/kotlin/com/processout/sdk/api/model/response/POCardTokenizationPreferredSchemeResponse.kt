package com.processout.sdk.api.model.response

import com.processout.sdk.api.model.request.POCardTokenizationPreferredSchemeRequest
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import java.util.UUID

/**
 * Defines the response with preferred scheme that will be used for card tokenization by default.
 * This response can only be created from [POCardTokenizationPreferredSchemeRequest.toResponse] function.
 *
 * @param[uuid] Unique identifier of response that must be equal to UUID of request.
 * @param[issuerInformation] Holds information about card issuing institution that issued the card to the card holder.
 * @param[preferredScheme] Preferred scheme that will be used by default for card tokenization.
 */
/** @suppress */
@ProcessOutInternalApi
data class POCardTokenizationPreferredSchemeResponse internal constructor(
    val uuid: UUID,
    val issuerInformation: POCardIssuerInformation,
    val preferredScheme: String?
)

/**
 * Creates [POCardTokenizationPreferredSchemeResponse] from [POCardTokenizationPreferredSchemeRequest].
 *
 * @param[preferredScheme] Preferred scheme that will be used by default for card tokenization. Will use a primary scheme if _null_.
 */
/** @suppress */
@ProcessOutInternalApi
fun POCardTokenizationPreferredSchemeRequest.toResponse(
    preferredScheme: String?
) = POCardTokenizationPreferredSchemeResponse(uuid, issuerInformation, preferredScheme)
