package com.processout.sdk.api.network

import android.app.Application

internal data class NetworkConfiguration(
    val application: Application,
    val sdkVersion: String,
    val baseUrl: String,
    val projectId: String,
    val privateKey: String = String()
)
