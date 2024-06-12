@file:Suppress("RestrictedApi")

package com.processout.sdk.configuration

import com.processout.sdk.BuildConfig
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.ProcessOutConfiguration

class TestConfiguration {

    companion object {
        fun configure(application: TestApplication) {
            ProcessOut.configure(
                ProcessOutConfiguration(
                    application,
                    projectId = BuildConfig.PROJECT_ID
                ).apply { privateKey = BuildConfig.PROJECT_KEY }
            )
        }
    }
}
