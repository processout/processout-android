package com.processout.sdk.ui.core.state

import androidx.compose.runtime.Stable
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
@Stable
data class POStableList<E>(
    val elements: List<E>
)
