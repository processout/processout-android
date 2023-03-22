package com.processout.sdk.api.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class POCustomerTokenRequest(
    // Payment source to use for authorization.
    val source: String? = null,
    // Enable 3DS2 or not.
    val threeDS2Enabled: Boolean? = null,
    // Verify the token or not.
    val verify: Boolean? = null,
    // Additional matadata.
    val metadata: Map<String, String>? = null,
    // Verify additional matadata.
    val verifyMetadata: Map<String, String>? = null,
    // Ondicate which third party 3ds2 SDK is used for the call.
    val thirdPartySDKVersion: String? = null,
    // Scheme the customer would prefer to process the transaction on.
    val preferredScheme: String? = null,
    // Verify invoice ID.
    val verificationInvoiceUID: String? = null,
    // Cancel manually invoice.
    val manualInvoiceCancellation: Boolean? = null,
    // Description that will be sent to the tokenization gateway service.
    val description: String? = null,
    // Return a given URL.
    val returnURL: String? = null,
    // Cancel a given URL.
    val cancelURL: String? = null,
)

@JsonClass(generateAdapter = true)
internal data class POCustomerTokenRequestWithDeviceData(
    // Payment source to use for authorization.
    val source: String? = null,
    // Enable 3DS2 or not.
    @Json(name = "enable_three_d_s_2")
    val threeDS2Enabled: Boolean? = null,
    // Verify the token or not.
    val verify: Boolean? = null,
    // Additional matadata.
    val metadata: Map<String, String>? = null,
    // Verify additional matadata.
    @Json(name = "verify_metadata")
    val verifyMetadata: Map<String, String>? = null,
    // Ondicate which third party 3ds2 SDK is used for the call.
    @Json(name = "third_party_sdk_version")
    val thirdPartySDKVersion: String? = null,
    // Scheme the customer would prefer to process the transaction on.
    @Json(name = "preferred_scheme")
    val preferredScheme: String? = null,
    // Verify invoice ID.
    @Json(name = "invoice_id")
    val verificationInvoiceUID: String? = null,
    // Cancel manually invoice.
    @Json(name = "manual_invoice_cancellation")
    val manualInvoiceCancellation: Boolean? = null,
    // Description that will be sent to the tokenization gateway service.
    val description: String? = null,
    // Return a given URL.
    @Json(name = "return_url")
    val returnURL: String? = null,
    // Cancel a given URL.
    @Json(name = "cancel_url")
    val cancelURL: String? = null,
    @Json(name = "device")
    val deviceData: PODeviceData? = null
)
