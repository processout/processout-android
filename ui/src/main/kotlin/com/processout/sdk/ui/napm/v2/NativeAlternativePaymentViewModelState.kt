package com.processout.sdk.ui.napm.v2

import androidx.compose.runtime.Immutable
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.state.POPhoneNumberFieldState
import com.processout.sdk.ui.shared.state.ConfirmationDialogState
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
        val image: Image?,
        val message: String,
        val primaryAction: POActionState?,
        val secondaryAction: POActionState?,
        val saveBarcodeAction: POActionState?,
        val confirmationDialog: ConfirmationDialogState?,
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
        data class CheckboxField(val state: FieldState) : Field
        data class PhoneNumberField(val state: POPhoneNumberFieldState) : Field
    }

    @Immutable
    sealed interface Image {
        data class Url(val value: String) : Image
        data class Bitmap(val value: android.graphics.Bitmap) : Image
    }
}
