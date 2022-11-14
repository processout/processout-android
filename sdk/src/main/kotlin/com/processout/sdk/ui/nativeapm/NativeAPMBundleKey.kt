package com.processout.sdk.ui.nativeapm

import com.processout.sdk.BuildConfig

internal object NativeAPMBundleKey {
    const val GATEWAY_CONFIGURATION_ID = "${BuildConfig.LIBRARY_PACKAGE_NAME}.GATEWAY_CONFIGURATION_ID"
    const val INVOICE_ID = "${BuildConfig.LIBRARY_PACKAGE_NAME}.INVOICE_ID"
}
