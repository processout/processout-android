package com.processout.sdk.ui.core.state

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
@Immutable
data class POActionState(
    val id: String,
    val text: String,
    val primary: Boolean,
    val enabled: Boolean = true,
    val loading: Boolean = false,
    @DrawableRes val iconResId: Int? = null,
    val confirmation: Confirmation? = null
) {

    @Immutable
    data class Confirmation(
        val title: String,
        val message: String?,
        val confirmActionText: String,
        val dismissActionText: String?
    )
}
