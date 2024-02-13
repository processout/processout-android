package com.processout.sdk.api.model.request

import com.processout.sdk.api.model.response.POCardIssuerInformation
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import java.util.UUID

/**
 * Defines the request to choose a preferred scheme that will be used for card tokenization by default.
 *
 * @param[issuerInformation] Holds information about card issuing institution that issued the card to the card holder.
 * @param[uuid] Unique identifier of request.
 */
/** @suppress */
@ProcessOutInternalApi
data class POCardTokenizationPreferredSchemeRequest @ProcessOutInternalApi constructor(
    val issuerInformation: POCardIssuerInformation,
    val uuid: UUID = UUID.randomUUID()
)
