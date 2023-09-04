package com.processout.sdk.api

import android.app.Application
import androidx.annotation.VisibleForTesting
import com.processout.sdk.core.annotation.ProcessOutInternalApi

/**
 * Defines ProcessOut configuration.
 *
 * @param[application] Instance of [Application].
 * @param[projectId] Project ID.
 * @param[debug] Enables debug mode. Default value is _false_. __Note:__ Debug logs may contain sensitive data.
 */
data class ProcessOutConfiguration(
    val application: Application,
    val projectId: String,
    val debug: Boolean = false
) {
    /**
     * __Warning:__ Intended to be used only for testing purposes.
     * Storing private key inside application is extremely dangerous and is highly discouraged.
     */
    @ProcessOutInternalApi
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    var privateKey: String = String()
}
