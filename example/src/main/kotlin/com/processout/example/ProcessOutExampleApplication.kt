package com.processout.example

import android.app.Application
import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.ProcessOutApiConfiguration

class ProcessOutExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ProcessOutApi.configure(
            ProcessOutApiConfiguration(
                this,
                "proj_"
            )
        )
    }
}
