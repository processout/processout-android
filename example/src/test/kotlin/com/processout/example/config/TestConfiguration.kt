package com.processout.example.config

import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.ProcessOutApiConfiguration

class TestConfiguration {

    companion object {
        fun configure(application: TestApplication) {
            ProcessOut.configure(
                ProcessOutApiConfiguration(
                    application,
                    "test-proj_2hO7lwt5vf3FjBFB37glPzMG3Y8Lq8O8",
                    "key_test_R56fdFWMpcAzt5Cenn3oK4emCowFe4l4"
                )
            )
        }
    }
}
