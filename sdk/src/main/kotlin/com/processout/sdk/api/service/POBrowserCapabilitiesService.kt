package com.processout.sdk.api.service

/**
 * Allows to check supported browser capabilities.
 */
interface POBrowserCapabilitiesService {

    companion object {
        const val CHROME_PACKAGE = "com.android.chrome"
    }

    /**
     * Returns _true_ is Custom Chrome Tabs is supported on device.
     */
    fun isCustomTabsSupported(): Boolean
}
