package com.processout.sdk.core.locale

import android.app.Application
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import com.processout.sdk.R
import java.util.*

internal fun Application.currentAppLocale(): Locale =
    ConfigurationCompat.getLocales(resources.configuration)[0]
        ?: LocaleListCompat.getAdjustedDefault()[0]
        ?: Locale.getDefault()

internal fun Application.currentSdkLocale(): Locale =
    currentAppLocale().let {
        if (it.toLanguageTag().startsWith(getString(R.string.po_locale_language_code)))
            it else Locale.ENGLISH
    }
