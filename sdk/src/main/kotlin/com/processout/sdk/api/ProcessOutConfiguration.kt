package com.processout.sdk.api

import android.app.Application
import androidx.annotation.VisibleForTesting
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import java.util.UUID

/**
 * Defines ProcessOut configuration.
 *
 * @param[application] Instance of the [Application].
 * @param[projectId] Project ID.
 * @param[debug] Enables debug mode. Default value is _false_. __Note:__ debug logs may contain sensitive data.
 * @param[enableTelemetry] Enables sending telemetry data to ProcessOut. Default value is _false_.
 * @param[applicationInformation] Application information that helps ProcessOut to troubleshoot potential issues.
 */
data class ProcessOutConfiguration(
    val application: Application,
    val projectId: String,
    val debug: Boolean = false,
    val enableTelemetry: Boolean = false,
    val applicationInformation: ApplicationInformation? = null
) {

    /**
     * Application information that helps ProcessOut to troubleshoot potential issues.
     *
     * @param[name] Application name.
     * @param[version] Application version.
     */
    data class ApplicationInformation(
        val name: String? = null,
        val version: String? = null
    )

    internal val sessionId = UUID.randomUUID().toString()

    /**
     * __Warning:__ Intended to be used only for testing purposes.
     * Storing private key inside application is extremely dangerous and is highly discouraged.
     * @suppress
     */
    @ProcessOutInternalApi
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    var privateKey: String = String()
}
