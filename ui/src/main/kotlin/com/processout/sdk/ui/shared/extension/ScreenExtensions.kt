package com.processout.sdk.ui.shared.extension

import android.content.Context
import android.os.Build
import android.util.Size
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Returns screen size in pixels.
 * Includes top status bar height, but excludes bottom navigation bar height.
 */
internal fun Context.screenSize(): Size {
    val windowManager = ContextCompat.getSystemService(this, WindowManager::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && windowManager != null) {
        with(windowManager.currentWindowMetrics) {
            val navigationBarHeight = windowInsets.getInsets(
                WindowInsetsCompat.Type.navigationBars()
            ).bottom
            return Size(bounds.width(), bounds.height() - navigationBarHeight)
        }
    } else {
        with(resources.displayMetrics) {
            return Size(widthPixels, heightPixels)
        }
    }
}
