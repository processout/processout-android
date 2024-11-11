package com.processout.sdk.ui.shared.state

import androidx.compose.runtime.Immutable

@Immutable
data class ConfirmationDialogState(
    val id: String,
    val title: String,
    val message: String?,
    val confirmActionText: String,
    val dismissActionText: String?
)
