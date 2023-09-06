package com.processout.sdk.api.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request parameters used to assign new source to existing customer token and potentially verify it.
 *
 * @param customerId ID of the customer who token belongs to.
 * @param tokenId Token ID that belong to the customer.
 * @param source Payment source to associate with token. The source can be a card, an APM or a gateway request.
 * For the source to be valid, you must not have used it for any previous payment or to create any other customer tokens.
 * @param preferredScheme Card scheme or co-scheme that should get priority if it is available.
 * @param enableThreeDS2 Boolean value indicating whether 3DS2 is enabled. Default value is _true_.
 * @param verify Boolean value that indicates whether token should be verified.
 * Make sure to also pass valid [invoiceId] if you want verification to happen. Default value is _false_.
 * @param invoiceId Invoice identifier that will be used for token verification.
 * @param thirdPartySdkVersion Can be used for a 3DS2 request to indicate which third party SDK is used for the call.
 * @param metadata Additional metadata.
 */
data class POAssignCustomerTokenRequest(
    val customerId: String,
    val tokenId: String,
    val source: String,
    val preferredScheme: String? = null,
    val enableThreeDS2: Boolean = true,
    val verify: Boolean = false,
    val invoiceId: String? = null,
    val thirdPartySdkVersion: String? = null,
    val metadata: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
internal data class POAssignCustomerTokenRequestWithDeviceData(
    val source: String,
    @Json(name = "preferred_scheme")
    val preferredScheme: String?,
    @Json(name = "enable_three_d_s_2")
    val enableThreeDS2: Boolean,
    val verify: Boolean,
    @Json(name = "invoice_id")
    val invoiceId: String?,
    @Json(name = "third_party_sdk_version")
    val thirdPartySdkVersion: String?,
    val metadata: Map<String, String>?,
    @Json(name = "device")
    val deviceData: PODeviceData
)
