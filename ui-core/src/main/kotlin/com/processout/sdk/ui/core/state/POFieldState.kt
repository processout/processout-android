package com.processout.sdk.ui.core.state

import androidx.annotation.DrawableRes
import androidx.compose.foundation.text.KeyboardOptions
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.formatter.POFormatter

/** @suppress */
@ProcessOutInternalApi
data class POFieldState(
    val key: String,
    val value: String = String(),
    val title: String? = null,
    val description: String? = null,
    val placeholder: String? = null,
    @DrawableRes
    val iconResId: Int? = null,
    val formatter: POFormatter? = null,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    val enabled: Boolean = true,
    val isError: Boolean = false
)
