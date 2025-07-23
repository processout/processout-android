package com.processout.sdk.ui.napm

import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.shared.state.FieldValue

internal sealed interface NativeAlternativePaymentEvent {
    data class FieldValueChanged(val id: String, val value: FieldValue) : NativeAlternativePaymentEvent
    data class FieldFocusChanged(val id: String, val isFocused: Boolean) : NativeAlternativePaymentEvent
    data class Action(val id: String) : NativeAlternativePaymentEvent
    data class DialogAction(val id: String, val isConfirmed: Boolean) : NativeAlternativePaymentEvent
    data class ActionConfirmationRequested(val id: String) : NativeAlternativePaymentEvent
    data class PermissionRequestResult(val permission: String, val isGranted: Boolean) : NativeAlternativePaymentEvent
    data class Dismiss(val failure: ProcessOutResult.Failure) : NativeAlternativePaymentEvent
}

internal sealed interface NativeAlternativePaymentSideEffect {
    data class PermissionRequest(val permission: String) : NativeAlternativePaymentSideEffect
}

internal sealed interface NativeAlternativePaymentCompletion {
    data object Awaiting : NativeAlternativePaymentCompletion
    data object Success : NativeAlternativePaymentCompletion
    data class Failure(val failure: ProcessOutResult.Failure) : NativeAlternativePaymentCompletion
}
