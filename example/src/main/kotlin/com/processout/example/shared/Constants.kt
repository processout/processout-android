package com.processout.example.shared

import com.processout.example.BuildConfig

object Constants {
    const val DEFAULT_RETURN_URL = "${BuildConfig.APPLICATION_ID}://processout/return"
    const val MERCHANT_RETURN_URL = "merchant://example/return"
}
