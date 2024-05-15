package com.processout.sdk.ui.shared.model

internal data class ActionConfirmation(
    val enabled: Boolean,
    val title: String,
    val message: String?,
    val positiveActionText: String,
    val negativeActionText: String?
)
