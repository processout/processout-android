package com.processout.sdk.ui.core.state

import androidx.annotation.DrawableRes
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
data class POFieldState(
    val key: String,
    val value: String = String(),
    val title: String? = null,
    val description: String? = null,
    val placeholder: String? = null,
    val enabled: Boolean = true,
    val isError: Boolean = false,
    @DrawableRes
    val iconResId: Int? = null
)
