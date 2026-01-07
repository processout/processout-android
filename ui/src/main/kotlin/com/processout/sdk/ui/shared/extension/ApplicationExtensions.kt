package com.processout.sdk.ui.shared.extension

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.core.net.toUri
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import com.processout.sdk.core.logger.POLogger
import java.util.Locale

internal fun Application.currentAppLocale(): Locale =
    ConfigurationCompat.getLocales(resources.configuration)[0]
        ?: LocaleListCompat.getAdjustedDefault()[0]
        ?: Locale.getDefault()

internal fun Application.openDeepLink(uri: String): Boolean {
    try {
        val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        return true
    } catch (e: ActivityNotFoundException) {
        POLogger.warn("Failed to open deep link [%s] with exception: %s", uri, e)
        return false
    }
}
