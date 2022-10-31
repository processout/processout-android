package com.processout.example.config

import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.core.exception.ProcessOutException

class TestConfiguration {

    companion object {
        fun configure(application: TestApplication) {
            try {
                ProcessOutApi.configure(
                    ProcessOutApi.Configuration(
                        application,
                        "test-proj",
                        "key_test"
                    )
                )
            } catch (_: ProcessOutException) {
                // ignore
            }
        }
    }
}
