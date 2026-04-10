package com.processout.example.shared

import com.processout.example.BuildConfig

object Constants {
    const val RETURN_URL_DEFAULT = "${BuildConfig.APPLICATION_ID}://processout/return"
    const val RETURN_URL_APP_LINK = "https://merchant-example.com/return"
    const val RETURN_URL_DEEP_LINK = "merchant://example/return"
}
