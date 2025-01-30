package com.processout.sdk.ui.core.state

import androidx.compose.runtime.Immutable
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.shared.image.PODrawableImage

/** @suppress */
@ProcessOutInternalApi
@Immutable
data class POActionState(
    val id: String,
    val text: String,
    val primary: Boolean,
    val enabled: Boolean = true,
    val loading: Boolean = false,
    val icon: PODrawableImage? = null,
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
