package com.processout.sdk.api.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class POInvoiceAuthorizationRequest(
    // Payment source to use for authorization.
    val source: String,
    // Boolean value indicating if authorization is incremental.
    val incremental: Boolean? = null,
    // Boolean value indicating whether 3DS2 is enabled.
    val enableThreeDS2: Boolean? = null,
    // Card scheme or co-scheme that should get priority if it is available.
    val preferredScheme: String? = null,
    // Can be used for a 3DS2 request to indicate which third party SDK is used for the call.
    val thirdPartySdkVersion: String? = null,
    // Can be used to to provide specific ids to indicate which of items provided in invoice details list
    // are subject to capture.
    val invoiceDetailsIds: List<String>? = null,
    // Allows to specify if transaction blocking due to MasterCard Merchant Advice Code should be applied or not.
    val overrideMacBlocking: Boolean? = null,
    // Allows to specify which scheme ID to use for subsequent CIT/MITs if applicable.
    val initialSchemeTransactionId: String? = null,
    // Additional matadata.
    val metadata: Map<String, String>? = null,
)


@JsonClass(generateAdapter = true)
internal data class POInvoiceAuthorizationRequestWithDeviceData(
    val source: String,
    val incremental: Boolean? = null,
    @Json(name = "enable_three_d_s_2")
    val enableThreeDS2: Boolean? = null,
    @Json(name = "preferred_scheme")
    val preferredScheme: String? = null,
    @Json(name = "third_party_sdk_version")
    val thirdPartySdkVersion: String? = null,
    @Json(name = "invoice_detail_ids")
    val invoiceDetailsIds: List<String>? = null,
    @Json(name = "override_mac_blocking")
    val overrideMacBlocking: Boolean? = null,
    @Json(name = "initial_scheme_transaction_id")
    val initialSchemeTransactionId: String? = null,
    val metadata: Map<String, String>? = null,
    @Json(name = "device")
    val deviceData: PODeviceData? = null
)
