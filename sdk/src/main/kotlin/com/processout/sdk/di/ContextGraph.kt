package com.processout.sdk.di

import android.app.Application
import android.os.Build
import android.util.Size
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.processout.sdk.api.model.request.DeviceData
import com.processout.sdk.core.locale.currentAppLocale
import java.util.Calendar

internal interface ContextGraph {
    val application: Application
    val deviceData: DeviceData
}

internal class DefaultContextGraph(
    override val application: Application
) : ContextGraph {

    private val screenSize: Size = application.screenSize()

    override val deviceData: DeviceData
        get() = provideDeviceData()

    private fun provideDeviceData(): DeviceData {
        val timeZoneOffset = Calendar.getInstance().let {
            -(it.get(Calendar.ZONE_OFFSET) + it.get(Calendar.DST_OFFSET)) / (1000 * 60)
        }
        return DeviceData(
            appLanguage = application.currentAppLocale().toLanguageTag(),
            appScreenWidth = screenSize.width,
            appScreenHeight = screenSize.height,
            appTimeZoneOffset = timeZoneOffset
        )
    }

    private fun Application.screenSize(): Size {
        val windowManager = ContextCompat.getSystemService(this, WindowManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && windowManager != null) {
            with(windowManager.currentWindowMetrics) {
                return Size(bounds.width(), bounds.height())
            }
        } else {
            with(resources.displayMetrics) {
                return Size(widthPixels, heightPixels)
            }
        }
    }
}
