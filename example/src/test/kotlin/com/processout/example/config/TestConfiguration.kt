package com.processout.example.config

import com.processout.sdk.api.ProcessOutApi

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
