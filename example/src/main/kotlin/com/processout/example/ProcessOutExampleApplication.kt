package com.processout.example

import android.app.Application
import com.processout.sdk.api.ProcessOutApi

class ProcessOutExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ProcessOutApi.configure(
            ProcessOutApi.Configuration(
                this,
                "proj_",
                "key_test"
            )
        )
    }
}
