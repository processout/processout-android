package com.processout.example

import android.annotation.SuppressLint
import android.app.Application
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.ProcessOutConfiguration

class ProcessOutExampleApplication : Application() {

    @SuppressLint("RestrictedApi")
    override fun onCreate() {
        super.onCreate()
        ProcessOut.configure(
            ProcessOutConfiguration(
                application = this,
                projectId = BuildConfig.PROJECT_ID,
                debug = true
            ).apply { privateKey = BuildConfig.PROJECT_KEY }
        )
    }
}
