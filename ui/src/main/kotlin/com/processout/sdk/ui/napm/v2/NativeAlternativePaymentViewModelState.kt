package com.processout.sdk.ui.napm.v2

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable
import com.processout.sdk.api.model.response.POImageResource
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.state.POPhoneNumberFieldState
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentInteractorState.Stepper
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
    data class Loaded(
        val logo: POImageResource,
        val title: String?,
        val content: Content,
        val primaryAction: POActionState?,
        val secondaryAction: POActionState?
    ) : NativeAlternativePaymentViewModelState

    //endregion

    @Immutable
    sealed interface Content {

        @Immutable
        data class NextStep(
            val elements: POImmutableList<Element>,
            val focusedFieldId: String?
        ) : Content

        @Immutable
        data class Pending(
            val stepper: Stepper?,
            val elements: POImmutableList<Element>?
        ) : Content

        @Immutable
        data class Completed(
            val elements: POImmutableList<Element>?
        ) : Content
    }

    @Immutable
    sealed interface Element {

        @Immutable
        data class TextField(val state: FieldState) : Element

        @Immutable
        data class CodeField(val state: FieldState) : Element

        @Immutable
        data class RadioField(val state: FieldState) : Element

        @Immutable
        data class DropdownField(val state: FieldState) : Element

        @Immutable
        data class CheckboxField(val state: FieldState) : Element

        @Immutable
        data class PhoneNumberField(val state: POPhoneNumberFieldState) : Element

        @Immutable
        data class InstructionMessage(val label: String?, val value: String) : Element

        @Immutable
        data class Image(val value: POImageResource) : Element

        @Immutable
        data class Barcode(
            val image: Bitmap,
            val saveBarcodeAction: POActionState,
            val confirmationDialog: ConfirmationDialogState?
        ) : Element
    }
}
