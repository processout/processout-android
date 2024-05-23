package com.processout.sdk.ui.napm

import androidx.compose.runtime.Immutable
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POFieldState
import com.processout.sdk.ui.core.state.POImmutableList

@Immutable
internal sealed interface NativeAlternativePaymentViewModelState {

    //region States

    data object Loading : NativeAlternativePaymentViewModelState

    @Immutable
    data class UserInput(
        val title: String,
        val fields: POImmutableList<Field>,
        val focusedFieldId: String?,
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

    //endregion

    @Immutable
    sealed interface Field {
        data class TextField(val state: POFieldState) : Field
        data class CodeField(val state: POFieldState) : Field
        data class RadioField(val state: POFieldState) : Field
        data class DropdownField(val state: POFieldState) : Field
    }
}
