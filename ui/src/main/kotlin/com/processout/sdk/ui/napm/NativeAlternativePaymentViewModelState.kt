package com.processout.sdk.ui.napm

import androidx.compose.runtime.Immutable
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.shared.state.FieldState

@Immutable
internal sealed interface NativeAlternativePaymentViewModelState {

    //region States

    @Immutable
    data class Loading(
        val secondaryAction: POActionState?
    ) : NativeAlternativePaymentViewModelState

    @Immutable
    data class UserInput(
        val title: String,
        val fields: POImmutableList<Field>,
        val focusedFieldId: String?,
        val primaryAction: POActionState,
        val secondaryAction: POActionState?
    ) : NativeAlternativePaymentViewModelState

    @Immutable
    data class Capture(
        val title: String?,
        val logoUrl: String?,
        val imageUrl: String?,
        val message: String,
        val primaryAction: POActionState?,
        val secondaryAction: POActionState?,
        val withProgressIndicator: Boolean,
        val isCaptured: Boolean
    ) : NativeAlternativePaymentViewModelState

    //endregion

    @Immutable
    sealed interface Field {
        data class TextField(val state: FieldState) : Field
        data class CodeField(val state: FieldState) : Field
        data class RadioField(val state: FieldState) : Field
        data class DropdownField(val state: FieldState) : Field
    }
}
