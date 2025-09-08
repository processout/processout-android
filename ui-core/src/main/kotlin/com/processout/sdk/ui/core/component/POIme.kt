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
        val rootView = LocalView.current.rootView
        DisposableEffect(rootView) {
            val listener = ViewTreeObserver.OnGlobalLayoutListener {
                isImeVisible.value = ViewCompat.getRootWindowInsets(rootView)
                    ?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
            }
            rootView.viewTreeObserver
                .takeIf { it.isAlive }
                ?.addOnGlobalLayoutListener(listener)
            onDispose {
                rootView.viewTreeObserver
                    .takeIf { it.isAlive }
                    ?.removeOnGlobalLayoutListener(listener)
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
