package com.processout.sdk.api

import android.app.Application
import com.processout.sdk.core.annotation.ProcessOutInternalApi

data class ProcessOutConfiguration(
    val application: Application,
    val projectId: String
) {
    @ProcessOutInternalApi
    internal val privateKey: String = String()
}
