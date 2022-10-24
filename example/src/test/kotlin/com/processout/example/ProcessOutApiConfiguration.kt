package com.processout.example

import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.core.exception.ProcessOutException

class ProcessOutApiConfiguration {

    companion object {
        fun configure() {
            try {
                ProcessOutApi.configure(
                    ProcessOutApi.Configuration(
                        "test-proj"
                    )
                )
            } catch (_: ProcessOutException) {
                // ignore
            }
        }
    }
}
