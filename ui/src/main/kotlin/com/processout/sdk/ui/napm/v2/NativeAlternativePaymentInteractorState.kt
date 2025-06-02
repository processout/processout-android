package com.processout.sdk.ui.napm.v2

import android.graphics.Bitmap
import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.api.model.response.POBarcode.BarcodeType
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentNextStep.SubmitData.Parameter
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentNextStep.SubmitData.Parameter.*
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentInteractorState.*
import kotlinx.coroutines.flow.MutableStateFlow

internal sealed interface NativeAlternativePaymentInteractorState {

    //region States

    data object Idle : NativeAlternativePaymentInteractorState

    data object Loading : NativeAlternativePaymentInteractorState

    data class Loaded(
        val value: UserInputStateValue
    ) : NativeAlternativePaymentInteractorState

    data class UserInput(
        val value: UserInputStateValue
    ) : NativeAlternativePaymentInteractorState

    data class Submitted(
        val value: UserInputStateValue
    ) : NativeAlternativePaymentInteractorState

    data class Capturing(
        val value: CaptureStateValue
    ) : NativeAlternativePaymentInteractorState

    data class Captured(
        val value: CaptureStateValue
    ) : NativeAlternativePaymentInteractorState

    //endregion

    data class UserInputStateValue(
//        val invoice: Invoice, // TODO(v2)
//        val gateway: Gateway, // TODO(v2)
        val fields: List<Field>,
        val focusedFieldId: String?,
        val primaryActionId: String,
        val secondaryAction: Action,
        val submitAllowed: Boolean,
        val submitting: Boolean
    )

    data class CaptureStateValue(
        val paymentProviderName: String?,
        val logoUrl: String?,
        val customerAction: CustomerAction?,
        val primaryActionId: String?,
        val secondaryAction: Action,
        val withProgressIndicator: Boolean
    )

    data class CustomerAction(
        val message: String,
        val imageUrl: String?,
        val barcode: Barcode?
    )

    data class Barcode(
        val type: BarcodeType,
        val bitmap: Bitmap,
        val actionId: String,
        val confirmErrorActionId: String,
        val isError: Boolean = false
    )

    data class Field(
        val parameter: Parameter,
        val value: TextFieldValue,
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
    crossinline block: (stateValue: UserInputStateValue) -> Unit
) {
    val state = value
    if (state is Loaded) {
        block(state.value)
    }
}

internal inline fun MutableStateFlow<NativeAlternativePaymentInteractorState>.whenUserInput(
    crossinline block: (stateValue: UserInputStateValue) -> Unit
) {
    val state = value
    if (state is UserInput) {
        block(state.value)
    }
}

internal inline fun MutableStateFlow<NativeAlternativePaymentInteractorState>.whenSubmitted(
    crossinline block: (stateValue: UserInputStateValue) -> Unit
) {
    val state = value
    if (state is Submitted) {
        block(state.value)
    }
}

internal inline fun MutableStateFlow<NativeAlternativePaymentInteractorState>.whenCapturing(
    crossinline block: (stateValue: CaptureStateValue) -> Unit
) {
    val state = value
    if (state is Capturing) {
        block(state.value)
    }
}
