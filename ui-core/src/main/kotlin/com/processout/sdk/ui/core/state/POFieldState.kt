package com.processout.sdk.ui.core.state

import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
data class POFieldState(
    val value: String = String(),
    val title: String,
    val description: String = String(),
    val placeholder: String = String(),
    val enabled: Boolean = true,
    val isError: Boolean = false
)
