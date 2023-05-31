package com.processout.sdk.api.model.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Request parameters that are used to create a redirect URL to initiate alternative payment.
 * __Note:__ make sure to supply proper [additionalData] specific for particular payment method.
 *
 * @param invoiceId Invoice identifier for the APM payment.
 * @param gatewayConfigurationId Gateway configuration ID of the APM.
 * @param additionalData Additional data that will be supplied to the APM.
 * @param customerId Customer ID that may be used for creating APM recurring token.
 * @param tokenId Customer token ID that may be used for creating APM recurring token.
 */
@Parcelize
data class POAlternativePaymentMethodRequest(
    val invoiceId: String,
    val gatewayConfigurationId: String,
    val additionalData: Map<String, String>? = null,
    val customerId: String? = null,
    val tokenId: String? = null
) : Parcelable
