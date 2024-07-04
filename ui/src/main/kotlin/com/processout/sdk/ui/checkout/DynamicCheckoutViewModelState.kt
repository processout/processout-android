package com.processout.sdk.ui.checkout

import androidx.compose.runtime.Immutable

@Immutable
internal sealed interface DynamicCheckoutViewModelState {

    @Immutable
    data object Starting : DynamicCheckoutViewModelState

    @Immutable
    data object Started : DynamicCheckoutViewModelState

    @Immutable
    data object Success : DynamicCheckoutViewModelState
}
