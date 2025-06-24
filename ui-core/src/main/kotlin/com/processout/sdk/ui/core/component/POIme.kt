package com.processout.sdk.ui.core.component

import android.view.ViewTreeObserver
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
object POIme {

    @Composable
    fun isImeVisibleAsState(
        policy: SnapshotMutationPolicy<Boolean> = neverEqualPolicy()
    ): State<Boolean> {
        val isImeVisible = remember {
            mutableStateOf(
                value = false,
                policy = policy
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
    fun imeHeight(): Int =
        ViewCompat.getRootWindowInsets(LocalView.current.rootView)
            ?.getInsets(WindowInsetsCompat.Type.ime())
            ?.bottom ?: 0
}
