package com.processout.sdk.ui.napm

import com.processout.sdk.ui.core.state.POActionState

internal data class NativeAlternativePaymentViewModelState(
    val title: String,
    val primaryAction: POActionState,
    val secondaryAction: POActionState?
)
