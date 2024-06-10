package com.processout.sdk.api.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class TelemetryRequest(
    val events: List<Event>,
    val metadata: Metadata
) {

    /**
     * @param[timestamp] RFC3339 encoded timestamp.
     * @param[level] Event level: debug, info, warn, error.
     */
    @JsonClass(generateAdapter = true)
    data class Event(
        val timestamp: String,
        val level: String,
        val message: String,
        @Json(name = "gateway_configuration_id")
        val gatewayConfigurationId: String?,
        @Json(name = "customer_id")
        val customerId: String?,
        @Json(name = "customer_token_id")
        val customerTokenId: String?,
        @Json(name = "card_id")
        val cardId: String?,
        @Json(name = "invoice_id")
        val invoiceId: String?,
        val attributes: Map<String, String>
    )

    @JsonClass(generateAdapter = true)
    data class Metadata(
        val application: ApplicationMetadata,
        val device: DeviceMetadata
    )

    @JsonClass(generateAdapter = true)
    data class ApplicationMetadata(
        val name: String?,
        val version: String?
    )

    /**
     * @param[language] Default locale of the client device.
     * @param[timeZone] UTC offset of the device time zone.
     */
    @JsonClass(generateAdapter = true)
    data class DeviceMetadata(
        val language: String,
        val model: String,
        @Json(name = "time_zone")
        val timeZone: Int
    )
}
