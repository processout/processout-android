package com.processout.sdk.config

import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.config.TestApplication

class TestConfiguration {

    companion object {
        fun configure(application: TestApplication) {
            ProcessOutApi.configure(
                ProcessOutApi.Configuration(
                    application,
                    "test-proj",
                    "key_test"
                )
            )
        }
    }
}
