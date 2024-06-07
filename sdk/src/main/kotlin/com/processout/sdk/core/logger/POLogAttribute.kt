package com.processout.sdk.core.logger

import com.processout.sdk.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
object POLogAttribute {
    // Generic
    const val FILE = "File"
    const val LINE = "Line"

    // Specific
    const val IIN = "IIN"
    const val CARD_ID = "CardId"
    const val INVOICE_ID = "InvoiceId"
    const val CUSTOMER_ID = "CustomerId"
    const val CUSTOMER_TOKEN_ID = "CustomerTokenId"
    const val GATEWAY_CONFIGURATION_ID = "GatewayConfigurationId"
}
