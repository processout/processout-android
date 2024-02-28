package com.processout.sdk.ui.shared.extension

import android.app.Application
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import java.util.Locale

internal fun Application.currentAppLocale(): Locale =
    ConfigurationCompat.getLocales(resources.configuration)[0]
        ?: LocaleListCompat.getAdjustedDefault()[0]
        ?: Locale.getDefault()
