package com.processout.sdk.ui.napm

import com.processout.sdk.ui.core.state.POActionState

internal sealed interface NativeAlternativePaymentViewModelState {
    data object Loading : NativeAlternativePaymentViewModelState

    data class UserInput(
        val title: String,
        val primaryAction: POActionState,
        val secondaryAction: POActionState?
    ) : NativeAlternativePaymentViewModelState

    data class Capture(
        val secondaryAction: POActionState?
    ) : NativeAlternativePaymentViewModelState
}
