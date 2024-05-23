package com.processout.sdk.ui.napm

import androidx.compose.runtime.Immutable
import com.processout.sdk.ui.core.state.POActionState

@Immutable
internal sealed interface NativeAlternativePaymentViewModelState {
    data object Loading : NativeAlternativePaymentViewModelState

    @Immutable
    data class UserInput(
        val title: String,
        val primaryAction: POActionState,
        val secondaryAction: POActionState?,
        val actionMessageMarkdown: String?,
        val actionImageUrl: String?,
        val successMessage: String
    ) : NativeAlternativePaymentViewModelState

    @Immutable
    data class Capture(
        val paymentProviderName: String?,
        val logoUrl: String?,
        val secondaryAction: POActionState?,
        val isCaptured: Boolean
    ) : NativeAlternativePaymentViewModelState
}
