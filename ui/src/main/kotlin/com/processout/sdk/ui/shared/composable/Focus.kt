package com.processout.sdk.ui.shared.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.job

@Composable
internal fun RequestFocus(
    focusRequester: FocusRequester,
    lifecycleEvent: Lifecycle.Event? = null
) {
    LaunchedEffect(lifecycleEvent) {
        coroutineContext.job.invokeOnCompletion {
            focusRequester.requestFocus()
        }
    }
}
