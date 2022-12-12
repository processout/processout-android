package com.processout.sdk.api.model.response

import com.processout.sdk.api.model.request.POCustomerAction
import com.processout.sdk.api.model.request.POCustomerActionResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class POCustomerTokenResponse(
    val customerAction: POCustomerAction? = null,
    val token: POToken
)

@JsonClass(generateAdapter = true)
data class POToken(
    val id: String,
    val type: String,
    val description: String?,
    val summary: String?,
    @Json(name = "verification_status")
    val verificationStatus: String?,
    @Json(name = "is_chargeable")
    val isChargeable: Boolean,
    @Json(name = "manual_invoice_cancellation")
    val manualInvoiceCancellation: String?,
    @Json(name = "can_get_balance")
    val canGetBalance: Boolean?,
    val metadata: Map<String, String>? = null,
)

data class POCustomerToken(
    val customerToken: POToken,
    val customerAction: POCustomerActionResponse?
)
