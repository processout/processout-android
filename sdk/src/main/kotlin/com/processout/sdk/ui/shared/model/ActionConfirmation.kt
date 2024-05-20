package com.processout.sdk.ui.shared.model

internal data class ActionConfirmation(
    val enabled: Boolean,
    val title: String,
    val message: String?,
    val confirmActionText: String,
    val dismissActionText: String?
)
