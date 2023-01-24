package com.processout.sdk.di

import android.app.Application
import android.os.Build
import com.processout.sdk.api.model.request.PODeviceData
import java.util.*

internal interface ContextGraph {
    val deviceData: PODeviceData
}

internal class ContextGraphImpl(private val application: Application) : ContextGraph {

    private fun provideDeviceData(): PODeviceData {
        // fetch timezone offset
        val calendar = Calendar.getInstance()
        val timeZoneOffSet = -(calendar.get(Calendar.ZONE_OFFSET) +
                calendar.get(Calendar.DST_OFFSET)) / (1000 * 60)

        val displayMetrics = application.resources.displayMetrics
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            application.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            application.resources.configuration.locale
        }

        return PODeviceData(
            locale.language,
            displayMetrics.widthPixels,
            displayMetrics.heightPixels,
            timeZoneOffSet
        )
    }

    override val deviceData: PODeviceData
        get() = provideDeviceData()
}
