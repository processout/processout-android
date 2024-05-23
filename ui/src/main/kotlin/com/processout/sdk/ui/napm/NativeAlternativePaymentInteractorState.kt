package com.processout.sdk.ui.napm

import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodTransactionDetails.Gateway
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodTransactionDetails.Invoice
import com.processout.sdk.ui.napm.NativeAlternativePaymentInteractorState.*
import kotlinx.coroutines.flow.MutableStateFlow

internal sealed interface NativeAlternativePaymentInteractorState {

    //region States

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
        val invoice: Invoice,
        val gateway: Gateway,
        val primaryActionId: String,
        val secondaryActionId: String,
        val submitAllowed: Boolean = true,
        val submitting: Boolean = false
    )

    data class CaptureStateValue(
        val paymentProviderName: String?,
        val logoUrl: String?,
        val secondaryActionId: String
    )

    object ActionId {
        const val SUBMIT = "submit"
        const val CANCEL = "cancel"
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
