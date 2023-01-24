package com.processout.sdk.config

import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.ProcessOutApiConfiguration

class TestConfiguration {

    companion object {
        fun configure(application: TestApplication) {
            ProcessOutApi.configure(
                ProcessOutApiConfiguration(
                    application,
                    "test-proj_2hO7lwt5vf3FjBFB37glPzMG3Y8Lq8O8",
                    "key_test_R56fdFWMpcAzt5Cenn3oK4emCowFe4l4"
                )
            )
        }
    }
}
