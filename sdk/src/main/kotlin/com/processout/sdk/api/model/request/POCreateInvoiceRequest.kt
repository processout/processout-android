package com.processout.sdk.api.model.request

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** @suppress */
@ProcessOutInternalApi
@JsonClass(generateAdapter = true)
data class POCreateInvoiceRequest(
    val name: String,
    val amount: String,
    val currency: String,
    @Json(name = "customer_id")
    val customerId: String? = null,
    @Json(name = "return_url")
    val returnUrl: String? = null,
    val shipping: POContact? = null,
    @Json(name = "payment_configuration")
    val paymentConfiguration: PaymentConfiguration? = null,
    val device: Map<String, String> = mapOf("channel" to "android"),
    val metadata: Map<String, String>? = null
) {

    @JsonClass(generateAdapter = true)
    data class PaymentConfiguration(
        val apm: AlternativePaymentConfiguration
    )

    @JsonClass(generateAdapter = true)
    data class AlternativePaymentConfiguration(
        @Json(name = "preferred_finalization_mode")
        val preferredFinalizationMode: PreferredFinalizationMode
    )

    @Suppress("EnumEntryName")
    @JsonClass(generateAdapter = false)
    enum class PreferredFinalizationMode {
        automatic, manual
    }
}
