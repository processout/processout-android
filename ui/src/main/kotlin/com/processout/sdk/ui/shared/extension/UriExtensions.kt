package com.processout.sdk.ui.shared.extension

import android.net.Uri

internal val Uri.isWeb: Boolean
    get() = scheme?.lowercase() in setOf("http", "https")
