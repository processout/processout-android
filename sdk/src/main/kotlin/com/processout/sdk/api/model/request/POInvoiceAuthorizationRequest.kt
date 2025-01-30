package com.processout.sdk.api.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

/**
 * Request parameters used for invoice authorization.
 *
 * @param invoiceId Invoice identifier to authorize.
 * @param source Payment source to use for authorization.
 * @param saveSource If you want us to save the payment source by creating a customer token during the authorization. Only supported with card payment source.
 * @param incremental Boolean value indicating if authorization is incremental. Default value is _false_.
 * @param enableThreeDS2 Boolean value indicating whether 3DS2 is enabled. Default value is _true_.
 * @param preferredScheme Card scheme or co-scheme that should get priority if it is available.
 * @param thirdPartySdkVersion Can be used for a 3DS2 request to indicate which third party SDK is used for the call.
 * @param invoiceDetailsIds Can be used to to provide specific ids to indicate which of items provided in invoice details list are subject to capture.
 * @param overrideMacBlocking Allows to specify if transaction blocking due to MasterCard Merchant Advice Code should be applied or not. Default value is _false_.
 * @param initialSchemeTransactionId Allows to specify which scheme ID to use for subsequent CIT/MITs if applicable.
 * @param autoCaptureAt You can set this property to arrange for the payment to be captured automatically after a time delay.
 * @param captureAmount Amount of money to capture when partial captures are available. Note that this only applies if you are also using the [autoCaptureAt] option.
 * @param authorizeOnly Boolean value indicating whether should only authorize the invoice or also capture it. Default value is _true_.
 * @param allowFallbackToSale Boolean value indicating whether should fallback to sale if the gateway does not support separation between authorization and capture. Default value is _false_.
 * @param clientSecret Client secret is a value of __X-ProcessOut-Client-Secret__ header of the invoice.
 * @param metadata Additional metadata.
 */
data class POInvoiceAuthorizationRequest(
    val invoiceId: String,
    val source: String,
    val saveSource: Boolean = false,
    val incremental: Boolean = false,
    val enableThreeDS2: Boolean = true,
    val preferredScheme: String? = null,
    val thirdPartySdkVersion: String? = null,
    val invoiceDetailsIds: List<String>? = null,
    val overrideMacBlocking: Boolean = false,
    val initialSchemeTransactionId: String? = null,
    val autoCaptureAt: Date? = null,
    val captureAmount: String? = null,
    val authorizeOnly: Boolean = true,
    val allowFallbackToSale: Boolean = false,
    val clientSecret: String? = null,
    val metadata: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
internal data class InvoiceAuthorizationRequestWithDeviceData(
    val source: String,
    @Json(name = "save_source")
    val saveSource: Boolean,
    val incremental: Boolean,
    @Json(name = "enable_three_d_s_2")
    val enableThreeDS2: Boolean,
    @Json(name = "preferred_scheme")
    val preferredScheme: String?,
    @Json(name = "third_party_sdk_version")
    val thirdPartySdkVersion: String?,
    @Json(name = "invoice_detail_ids")
    val invoiceDetailsIds: List<String>?,
    @Json(name = "override_mac_blocking")
    val overrideMacBlocking: Boolean,
    @Json(name = "initial_scheme_transaction_id")
    val initialSchemeTransactionId: String?,
    @Json(name = "auto_capture_at")
    val autoCaptureAt: Date?,
    @Json(name = "capture_amount")
    val captureAmount: String?,
    @Json(name = "authorize_only")
    val authorizeOnly: Boolean,
    @Json(name = "allow_fallback_to_sale")
    val allowFallbackToSale: Boolean,
    val metadata: Map<String, String>?,
    @Json(name = "device")
    val deviceData: DeviceData
)
