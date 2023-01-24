package com.processout.sdk.api

import android.app.Application

data class ProcessOutApiConfiguration(
    val application: Application,
    val projectId: String
) {
    /**
     * __Warning: only for testing purposes.__
     *
     * Storing private key inside application is extremely dangerous and highly discouraged.
     */
    internal val privateKey: String = String()
}
