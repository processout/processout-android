package com.processout.sdk.api.service

interface POBrowserCapabilitiesService {

    companion object {
        const val CHROME_PACKAGE = "com.android.chrome"
    }

    fun isCustomTabsSupported(): Boolean
}
