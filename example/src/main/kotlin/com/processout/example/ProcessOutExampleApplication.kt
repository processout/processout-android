package com.processout.example

import android.app.Application
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.ProcessOutConfiguration

class ProcessOutExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ProcessOut.configure(
            ProcessOutConfiguration(
                this,
                "proj_"
            )
        )
    }
}
