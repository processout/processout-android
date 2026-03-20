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

internal fun Application.openDeepLink(
    url: String,
    packageName: String? = null
): Boolean {
    try {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        packageName?.let { intent.setPackage(it) }
        startActivity(intent)
        return true
    } catch (e: ActivityNotFoundException) {
        POLogger.warn("Failed to open deep link [%s] for package [%s] with exception: %s", url, packageName, e)
        return false
    }
}

internal fun Application.openDeepLink(
    url: String,
    packageNames: Set<String>?
): Boolean {
    if (packageNames.isNullOrEmpty()) {
        if (url.toUri().isWeb) {
            POLogger.warn("Refused to open app link [%s] without a package, to prevent opening in an external browser.", url)
            return false
        }
        return openDeepLink(url)
    }
    return packageNames.any { packageName ->
        openDeepLink(url, packageName)
    }
}
