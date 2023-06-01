package com.processout.sdk.api.service

import android.app.Application
import android.content.ComponentName
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import com.processout.sdk.api.service.POBrowserCapabilitiesService.Companion.CHROME_PACKAGE

internal class BrowserCapabilitiesServiceImpl(
    private val application: Application
) : POBrowserCapabilitiesService {

    override fun isCustomTabsSupported(): Boolean =
        runCatching {
            CustomTabsClient.bindCustomTabsService(
                application,
                CHROME_PACKAGE,
                DefaultCustomTabsServiceConnection()
            )
        }.getOrDefault(false)

    private class DefaultCustomTabsServiceConnection : CustomTabsServiceConnection() {
        override fun onCustomTabsServiceConnected(
            name: ComponentName,
            client: CustomTabsClient
        ) = Unit

        override fun onServiceDisconnected(name: ComponentName) = Unit
    }
}
