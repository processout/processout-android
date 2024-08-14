package com.processout.sdk.api.model.request

import com.processout.sdk.api.model.response.POInvoice
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import java.util.UUID

/**
 * __Note:__ please make sure to invalidate existing invoice before creating the new one.
 */
/** @suppress */
@ProcessOutInternalApi
data class PODynamicCheckoutInvoiceRequest @ProcessOutInternalApi constructor(
    val uuid: UUID = UUID.randomUUID(),
    val invoice: POInvoice,
    val invalidationReason: PODynamicCheckoutInvoiceInvalidationReason
)
