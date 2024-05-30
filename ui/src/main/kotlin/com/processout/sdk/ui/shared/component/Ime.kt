package com.processout.sdk.ui.shared.component

import android.view.ViewTreeObserver
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

@Composable
internal fun isImeVisibleAsState(): State<Boolean> {
    val isImeVisible = remember {
        mutableStateOf(
            value = false,
            policy = neverEqualPolicy()
        )
    }
    val view = LocalView.current.rootView
    val viewTreeObserver = view.viewTreeObserver
    DisposableEffect(viewTreeObserver) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            isImeVisible.value = ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
        }
        viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
    return isImeVisible
}

@Composable
internal fun imeHeight(): Int =
    ViewCompat.getRootWindowInsets(LocalView.current.rootView)
        ?.getInsets(WindowInsetsCompat.Type.ime())
        ?.bottom ?: 0
