package com.processout.sdk.ui.shared.state

internal data class FieldState(
    val value: String = String(),
    val title: String,
    val description: String = String(),
    val placeholder: String = String(),
    val enabled: Boolean = true,
    val isError: Boolean = false
)
