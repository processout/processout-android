package com.processout.sdk.ui.napm

import com.processout.sdk.core.logger.POLogAttribute.CUSTOMER_ID
import com.processout.sdk.core.logger.POLogAttribute.CUSTOMER_TOKEN_ID
import com.processout.sdk.core.logger.POLogAttribute.GATEWAY_CONFIGURATION_ID
import com.processout.sdk.core.logger.POLogAttribute.INVOICE_ID
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.Flow.Authorization
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.Flow.Tokenization

internal val PONativeAlternativePaymentConfiguration.logAttributes: Map<String, String>
    get() = when (flow) {
        is Authorization -> mapOf(
            INVOICE_ID to flow.invoiceId,
            GATEWAY_CONFIGURATION_ID to flow.gatewayConfigurationId
        )
        is Tokenization -> mapOf(
            CUSTOMER_ID to flow.customerId,
            CUSTOMER_TOKEN_ID to flow.customerTokenId,
            GATEWAY_CONFIGURATION_ID to flow.gatewayConfigurationId
        )
    }
