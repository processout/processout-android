package com.processout.sdk.api.model.request.napm.v2

import android.os.Parcelable
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.parcelize.Parcelize

/**
 * Request parameters for native alternative payment authorization details.
 *
 * @param[invoiceId] Invoice identifier.
 * @param[gatewayConfigurationId] Gateway configuration identifier.
 */
/** @suppress */
@ProcessOutInternalApi
@Parcelize
data class PONativeAlternativePaymentAuthorizationDetailsRequest(
    val invoiceId: String,
    val gatewayConfigurationId: String
) : Parcelable
