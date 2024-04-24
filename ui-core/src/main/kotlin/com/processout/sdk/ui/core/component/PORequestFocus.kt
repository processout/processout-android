package com.processout.sdk.ui.core.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.Lifecycle
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import kotlinx.coroutines.job

/** @suppress */
@ProcessOutInternalApi
@Composable
fun PORequestFocus(
    focusRequester: FocusRequester,
    lifecycleEvent: Lifecycle.Event? = null
) {
    LaunchedEffect(lifecycleEvent) {
        coroutineContext.job.invokeOnCompletion {
            focusRequester.requestFocus()
        }
    }
}
