package com.processout.sdk.api.model.request

import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
internal data class LogRequest(
    val level: String,
    val tag: String,
    val message: String,
    val timestamp: Date,
    val attributes: Map<String, String>
)
