package com.processout.example

import android.app.Application
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.ProcessOutApiConfiguration

class ProcessOutExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ProcessOut.configure(
            ProcessOutApiConfiguration(
                this,
                "proj_"
            )
        )
    }
}
