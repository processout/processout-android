package com.processout.sdk.api.model.response

import com.processout.sdk.api.model.request.POCardTokenizationPreferredSchemeRequest
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import java.util.UUID

data class POCardTokenizationPreferredSchemeResponse internal constructor(
    val uuid: UUID,
    val issuerInformation: POCardIssuerInformation,
    val preferredScheme: String?
)

/** @suppress */
@ProcessOutInternalApi
fun POCardTokenizationPreferredSchemeRequest.toResponse(
    preferredScheme: String?
) = POCardTokenizationPreferredSchemeResponse(uuid, issuerInformation, preferredScheme)
