package com.processout.sdk.di

import android.app.Application
import com.processout.sdk.api.model.request.DeviceData
import com.processout.sdk.core.locale.currentAppLocale
import java.util.Calendar

internal interface ContextGraph {
    val application: Application
    val deviceData: DeviceData
}

internal class ContextGraphImpl(override val application: Application) : ContextGraph {

    override val deviceData: DeviceData
        get() = provideDeviceData()

    private fun provideDeviceData(): DeviceData {
        val displayMetrics = application.resources.displayMetrics
        val timezoneOffset = Calendar.getInstance().let {
            -(it.get(Calendar.ZONE_OFFSET) + it.get(Calendar.DST_OFFSET)) / (1000 * 60)
        }
        return DeviceData(
            application.currentAppLocale().toLanguageTag(),
            displayMetrics.widthPixels,
            displayMetrics.heightPixels,
            timezoneOffset
        )
    }
}
