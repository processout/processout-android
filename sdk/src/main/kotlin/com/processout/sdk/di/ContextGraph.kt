package com.processout.sdk.di

import android.app.Application
import com.processout.sdk.api.model.request.PODeviceData
import com.processout.sdk.core.locale.currentAppLocale
import java.util.Calendar

internal interface ContextGraph {
    val application: Application
    val deviceData: PODeviceData
}

internal class ContextGraphImpl(override val application: Application) : ContextGraph {

    override val deviceData: PODeviceData
        get() = provideDeviceData()

    private fun provideDeviceData(): PODeviceData {
        val displayMetrics = application.resources.displayMetrics
        val timezoneOffset = Calendar.getInstance().let {
            -(it.get(Calendar.ZONE_OFFSET) + it.get(Calendar.DST_OFFSET)) / (1000 * 60)
        }
        return PODeviceData(
            application.currentAppLocale().toLanguageTag(),
            displayMetrics.widthPixels,
            displayMetrics.heightPixels,
            timezoneOffset
        )
    }
}
