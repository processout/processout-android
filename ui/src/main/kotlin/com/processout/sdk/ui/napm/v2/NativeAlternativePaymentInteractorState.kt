package com.processout.sdk.ui.napm.v2

import android.graphics.Bitmap
import com.processout.sdk.api.model.response.POBarcode.BarcodeType
import com.processout.sdk.api.model.response.POImageResource
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse.Invoice
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentElement
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentElement.Form.Parameter
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentElement.Form.Parameter.*
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentMethodDetails
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentInteractorState.*
import com.processout.sdk.ui.shared.state.FieldValue
import kotlinx.coroutines.flow.MutableStateFlow

internal sealed interface NativeAlternativePaymentInteractorState {

    //region States

    data object Idle : NativeAlternativePaymentInteractorState

    data object Loading : NativeAlternativePaymentInteractorState

    data class Loaded(
        val value: NextStepStateValue
    ) : NativeAlternativePaymentInteractorState

    data class NextStep(
        val value: NextStepStateValue
    ) : NativeAlternativePaymentInteractorState

    data class Submitted(
        val value: NextStepStateValue
    ) : NativeAlternativePaymentInteractorState

    data class Pending(
        val value: PendingStateValue
    ) : NativeAlternativePaymentInteractorState

    data class Completed(
        val value: PendingStateValue
    ) : NativeAlternativePaymentInteractorState

    //endregion

    data class NextStepStateValue(
        val paymentMethod: PONativeAlternativePaymentMethodDetails,
        val invoice: Invoice?,
        val elements: List<Element>,
        val fields: List<Field>,
        val focusedFieldId: String?,
        val primaryActionId: String,
        val secondaryAction: Action,
        val submitAllowed: Boolean,
        val submitting: Boolean
    )

    data class PendingStateValue(
        val paymentMethod: PONativeAlternativePaymentMethodDetails,
        val invoice: Invoice?,
        val elements: List<Element>,
        val primaryActionId: String?,
        val secondaryAction: Action
    )

    sealed interface Element {

        data class Form(
            val form: PONativeAlternativePaymentElement.Form
        ) : Element

        data class Instruction(
            val instruction: NativeAlternativePaymentInteractorState.Instruction
        ) : Element

        data class InstructionGroup(
            val label: String?,
            val instructions: List<NativeAlternativePaymentInteractorState.Instruction>
        ) : Element
    }

    sealed interface Instruction {

        data class Message(
            val label: String?,
            val value: String
        ) : Instruction

        data class Image(
            val value: POImageResource
        ) : Instruction

        data class Barcode(
            val type: BarcodeType,
            val bitmap: Bitmap,
            val actionId: String,
            val confirmErrorActionId: String,
            val isError: Boolean = false
        ) : Instruction
    }

    data class Field(
        val parameter: Parameter,
        val value: FieldValue,
        val isValid: Boolean,
        val description: String?
    ) {
        val id: String
            get() = parameter.key

        val label: String
            get() = parameter.label

        val required: Boolean
            get() = parameter.required

        val minLength: Int?
            get() = when (parameter) {
                is Text -> parameter.minLength
                is Digits -> parameter.minLength
                is Card -> parameter.minLength
                is Otp -> parameter.minLength
                else -> null
            }

        val maxLength: Int?
            get() = when (parameter) {
                is Text -> parameter.maxLength
                is Digits -> parameter.maxLength
                is Card -> parameter.maxLength
                is Otp -> parameter.maxLength
                else -> null
            }
    }

    data class Action(
        val id: String,
        val enabled: Boolean
    )

    object ActionId {
        const val SUBMIT = "submit"
        const val CANCEL = "cancel"
        const val CONFIRM_PAYMENT = "confirm-payment"
        const val SAVE_BARCODE = "save-barcode"
        const val CONFIRM_SAVE_BARCODE_ERROR = "confirm-save-barcode-error"
    }
}

internal inline fun MutableStateFlow<NativeAlternativePaymentInteractorState>.whenLoaded(
    crossinline block: (stateValue: NextStepStateValue) -> Unit
) {
    val state = value
    if (state is Loaded) {
        block(state.value)
    }
}

internal inline fun MutableStateFlow<NativeAlternativePaymentInteractorState>.whenNextStep(
    crossinline block: (stateValue: NextStepStateValue) -> Unit
) {
    val state = value
    if (state is NextStep) {
        block(state.value)
    }
}

internal inline fun MutableStateFlow<NativeAlternativePaymentInteractorState>.whenSubmitted(
    crossinline block: (stateValue: NextStepStateValue) -> Unit
) {
    val state = value
    if (state is Submitted) {
        block(state.value)
    }
}

internal inline fun MutableStateFlow<NativeAlternativePaymentInteractorState>.whenPending(
    crossinline block: (stateValue: PendingStateValue) -> Unit
) {
    val state = value
    if (state is Pending) {
        block(state.value)
    }
}
