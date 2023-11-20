package com.processout.sdk.ui.core.state

import androidx.compose.runtime.Immutable
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
@Immutable
data class POImmutableCollection<T>(
    val elements: Collection<T>
)
