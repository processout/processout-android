package com.processout.sdk.api.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

/**
 * Request parameters for invoice authorization.
 *
 * @param[invoiceId] Identifier of invoice to authorize.
 * @param[source] Payment source to use for authorization.
 * @param[saveSource] Indicates whether we should save the payment source by creating a customer token during the authorization.
 * Default value is _false_.
 * @param[incremental] Indicates whether authorization is incremental.
 * Default value is _false_.
 * @param[enableThreeDS2] Indicates that request is coming directly from the frontend.
 * It is used to understand if we can instantly step-up to 3DS.
 * Value is always _true_.
 * @param[preferredScheme] Card scheme or co-scheme that should get priority if it is available.
 * @param[thirdPartySdkVersion] Can be used for a 3DS2 request to indicate which third party SDK is used for the call.
 * @param[invoiceDetailsIds] Can be used to to provide specific IDs to indicate which of items provided in invoice details list are subject to capture.
 * @param[overrideMacBlocking] Indicates whether transaction blocking should be applied due to MasterCard Merchant Advice Code.
 * Default value is _false_.
 * @param[initialSchemeTransactionId] Allows to specify which scheme ID to use for subsequent CIT/MITs if applicable.
 * @param[autoCaptureAt] You can set this property to arrange for the payment to be captured automatically after a time delay.
 * __Note:__ Preferably set auto capture date when creating the invoice.
 * @param[captureAmount] Amount of money to capture when partial captures are available.
 * __Note:__ This only applies if you are also using the [autoCaptureAt] option.
 * @param[authorizeOnly] Indicates that payment should be authorized without capturing.
 * __Note:__ You must capture the payment on the server if you use this option.
 * Value is always _true_.
 * @param[allowFallbackToSale] Indicates whether the payment should fallback to sale if the gateway does not support separation between authorization and capture.
 * Default value is _false_.
 * @param[clientSecret] Client secret is a value of __X-ProcessOut-Client-Secret__ header of the invoice.
 * @param[metadata] Operation metadata.
 */
data class POInvoiceAuthorizationRequest @Deprecated(message = "Use alternative constructor.") constructor(
    val invoiceId: String,
    val source: String,
    val saveSource: Boolean = false,
    val incremental: Boolean = false,
    @Deprecated(message = "This property is an implementation detail and shouldn't be used.")
    val enableThreeDS2: Boolean = true,
    val preferredScheme: String? = null,
    val thirdPartySdkVersion: String? = null,
    @Deprecated(message = "This property is only available when capturing the invoice.")
    val invoiceDetailsIds: List<String>? = null,
    val overrideMacBlocking: Boolean = false,
    val initialSchemeTransactionId: String? = null,
    val autoCaptureAt: Date? = null,
    val captureAmount: String? = null,
    @Deprecated(message = "This property is only available when capturing the invoice.")
    val authorizeOnly: Boolean = true,
    val allowFallbackToSale: Boolean = false,
    val clientSecret: String? = null,
    val metadata: Map<String, String>? = null
) {

    /**
     * Request parameters for invoice authorization.
     *
     * @param[invoiceId] Identifier of invoice to authorize.
     * @param[source] Payment source to use for authorization.
     * @param[saveSource] Indicates whether we should save the payment source by creating a customer token during the authorization.
     * Default value is _false_.
     * @param[incremental] Indicates whether authorization is incremental.
     * Default value is _false_.
     * @param[preferredScheme] Card scheme or co-scheme that should get priority if it is available.
     * @param[thirdPartySdkVersion] Can be used for a 3DS2 request to indicate which third party SDK is used for the call.
     * @param[overrideMacBlocking] Indicates whether transaction blocking should be applied due to MasterCard Merchant Advice Code.
     * Default value is _false_.
     * @param[initialSchemeTransactionId] Allows to specify which scheme ID to use for subsequent CIT/MITs if applicable.
     * @param[autoCaptureAt] You can set this property to arrange for the payment to be captured automatically after a time delay.
     * __Note:__ Preferably set auto capture date when creating the invoice.
     * @param[captureAmount] Amount of money to capture when partial captures are available.
     * __Note:__ This only applies if you are also using the [autoCaptureAt] option.
     * @param[allowFallbackToSale] Indicates whether the payment should fallback to sale if the gateway does not support separation between authorization and capture.
     * Default value is _false_.
     * @param[clientSecret] Client secret is a value of __X-ProcessOut-Client-Secret__ header of the invoice.
     * @param[metadata] Operation metadata.
     */
    constructor(
        invoiceId: String,
        source: String,
        saveSource: Boolean = false,
        incremental: Boolean = false,
        preferredScheme: String? = null,
        thirdPartySdkVersion: String? = null,
        overrideMacBlocking: Boolean = false,
        initialSchemeTransactionId: String? = null,
        autoCaptureAt: Date? = null,
        captureAmount: String? = null,
        allowFallbackToSale: Boolean = false,
        clientSecret: String? = null,
        metadata: Map<String, String>? = null
    ) : this(
        invoiceId = invoiceId,
        source = source,
        saveSource = saveSource,
        incremental = incremental,
        enableThreeDS2 = true,
        preferredScheme = preferredScheme,
        thirdPartySdkVersion = thirdPartySdkVersion,
        invoiceDetailsIds = null,
        overrideMacBlocking = overrideMacBlocking,
        initialSchemeTransactionId = initialSchemeTransactionId,
        autoCaptureAt = autoCaptureAt,
        captureAmount = captureAmount,
        authorizeOnly = true,
        allowFallbackToSale = allowFallbackToSale,
        clientSecret = clientSecret,
        metadata = metadata
    )
}

@JsonClass(generateAdapter = true)
internal data class InvoiceAuthorizationRequestWithDeviceData(
    val source: String,
    @Json(name = "save_source")
    val saveSource: Boolean,
    val incremental: Boolean,
    @Json(name = "enable_three_d_s_2")
    val enableThreeDS2: Boolean = true,
    @Json(name = "preferred_scheme")
    val preferredScheme: String?,
    @Json(name = "third_party_sdk_version")
    val thirdPartySdkVersion: String?,
    @Json(name = "override_mac_blocking")
    val overrideMacBlocking: Boolean,
    @Json(name = "initial_scheme_transaction_id")
    val initialSchemeTransactionId: String?,
    @Json(name = "auto_capture_at")
    val autoCaptureAt: Date?,
    @Json(name = "capture_amount")
    val captureAmount: String?,
    @Json(name = "allow_fallback_to_sale")
    val allowFallbackToSale: Boolean,
    val metadata: Map<String, String>?,
    @Json(name = "device")
    val deviceData: DeviceData
)
