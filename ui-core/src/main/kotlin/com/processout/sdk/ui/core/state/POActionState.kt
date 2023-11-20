package com.processout.sdk.ui.core.state

import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
data class POActionState(
    val text: String,
    val primary: Boolean,
    val enabled: Boolean = true,
    val loading: Boolean = false
)

/** @suppress */
@ProcessOutInternalApi
data class POActionStateExtended(
    val state: POActionState,
    val onClick: () -> Unit
)
