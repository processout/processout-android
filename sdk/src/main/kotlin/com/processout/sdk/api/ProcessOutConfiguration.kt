package com.processout.sdk.api

import android.app.Application
import androidx.annotation.VisibleForTesting
import com.processout.sdk.core.annotation.ProcessOutInternalApi

data class ProcessOutConfiguration(
    val application: Application,
    val projectId: String,
    val debug: Boolean = false
) {
    @ProcessOutInternalApi
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    var privateKey: String = String()
}
