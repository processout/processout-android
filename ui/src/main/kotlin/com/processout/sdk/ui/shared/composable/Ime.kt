package com.processout.sdk.ui.shared.composable

import android.view.ViewTreeObserver
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

@Composable
internal fun imeVisibleAsState(): State<Boolean> {
    val imeVisible = remember { mutableStateOf(false) }
    val view = LocalView.current.rootView
    val viewTreeObserver = view.viewTreeObserver
    DisposableEffect(viewTreeObserver) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            imeVisible.value = ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
        }
        viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
    return imeVisible
}

@Composable
internal fun imeHeight(): Int =
    ViewCompat.getRootWindowInsets(LocalView.current.rootView)
        ?.getInsets(WindowInsetsCompat.Type.ime())
        ?.bottom ?: 0