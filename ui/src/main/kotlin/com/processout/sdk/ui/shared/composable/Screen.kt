package com.processout.sdk.ui.shared.composable

import android.util.Size
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

@Composable
internal fun screenSize(): Size {
    val view = LocalView.current.rootView
    val insets = ViewCompat.getRootWindowInsets(view)
    val statusBarHeight = insets?.getInsets(
        WindowInsetsCompat.Type.statusBars()
    )?.top ?: 0
    val navigationBarHeight = insets?.getInsets(
        WindowInsetsCompat.Type.navigationBars()
    )?.bottom ?: 0
    val height = view.height - statusBarHeight - navigationBarHeight
    return Size(view.width, height)
}
