package com.processout.sdk.api.model.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class TelemetryResponse(
    val success: Boolean
)
