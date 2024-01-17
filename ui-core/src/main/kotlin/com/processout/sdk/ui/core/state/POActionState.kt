package com.processout.sdk.ui.core.state

import androidx.compose.runtime.Immutable
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
@Immutable
data class POActionState(
    val text: String,
    val primary: Boolean,
    val enabled: Boolean = true,
    val loading: Boolean = false
)

/** @suppress */
@ProcessOutInternalApi
@Immutable
data class POActionStateExtended(
    val state: POActionState,
    val onClick: () -> Unit
)
