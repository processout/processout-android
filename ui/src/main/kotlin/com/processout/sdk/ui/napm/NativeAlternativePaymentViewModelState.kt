package com.processout.sdk.ui.napm

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable
import com.processout.sdk.api.model.response.POImageResource
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.state.POPhoneNumberFieldState
import com.processout.sdk.ui.napm.NativeAlternativePaymentInteractorState.Stepper
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
        val header: Header?,
        val content: Content,
        val primaryAction: POActionState?,
        val secondaryAction: POActionState?
    ) : NativeAlternativePaymentViewModelState

    //endregion

    @Immutable
    data class Header(
        val logo: POImageResource,
        val title: String?
    )

    @Immutable
    data class Content(
        val uuid: String,
        val stage: Stage,
        val elements: POImmutableList<Element>?
    )

    @Immutable
    sealed interface Stage {

        @Immutable
        data class NextStep(
            val focusedFieldId: String?,
            val customContent: PONativeAlternativePaymentConfiguration.Content?
        ) : Stage

        @Immutable
        data class Pending(
            val stepper: Stepper?,
            val customContent: PONativeAlternativePaymentConfiguration.Content?
        ) : Stage

        @Immutable
        data class Completed(
            val title: String,
            val message: String?
        ) : Stage
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
        data class Message(val value: String) : InstructionElement

        @Immutable
        data class CopyableMessage(
            val label: String,
            val value: String,
            val copyText: String,
            val copiedText: String
        ) : InstructionElement

        @Immutable
        data class Image(val value: POImageResource) : InstructionElement

        @Immutable
        data class Barcode(
            val image: Bitmap,
            val saveBarcodeAction: POActionState,
            val confirmationDialog: ConfirmationDialogState?
        ) : InstructionElement

        @Immutable
        data class InstructionGroup(
            val label: String?,
            val instructions: POImmutableList<InstructionElement>
        ) : Element
    }

    @Immutable
    interface InstructionElement : Element
}
