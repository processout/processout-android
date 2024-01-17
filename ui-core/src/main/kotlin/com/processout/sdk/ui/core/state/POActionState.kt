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
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as POActionStateExtended
        return state == other.state
    }

    override fun hashCode(): Int {
        return state.hashCode()
    }
}
