package com.processout.sdk.api.network

internal data class NetworkConfiguration(
    val sdkVersion: String,
    val baseUrl: String,
    val projectId: String,
    val privateKey: String = String()
)
