package com.processout.sdk.api.model.request

import com.processout.sdk.api.model.response.POInvoice
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import java.util.UUID

/** @suppress */
@ProcessOutInternalApi
data class PODynamicCheckoutInvoiceRequest @ProcessOutInternalApi constructor(
    val uuid: UUID = UUID.randomUUID(),
    val invoice: POInvoice,
    val failure: ProcessOutResult.Failure
)
