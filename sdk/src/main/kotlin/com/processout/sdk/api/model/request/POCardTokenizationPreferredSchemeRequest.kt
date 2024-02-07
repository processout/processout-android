package com.processout.sdk.api.model.request

import com.processout.sdk.api.model.response.POCardIssuerInformation
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import java.util.UUID

/** @suppress */
@ProcessOutInternalApi
data class POCardTokenizationPreferredSchemeRequest @ProcessOutInternalApi constructor(
    val issuerInformation: POCardIssuerInformation,
    val uuid: UUID = UUID.randomUUID()
)
