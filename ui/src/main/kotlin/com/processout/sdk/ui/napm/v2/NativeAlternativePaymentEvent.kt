package com.processout.sdk.ui.napm.v2

import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.core.ProcessOutResult

internal sealed interface NativeAlternativePaymentEvent {
    data class FieldValueChanged(val id: String, val value: TextFieldValue) : NativeAlternativePaymentEvent
    data class FieldFocusChanged(val id: String, val isFocused: Boolean) : NativeAlternativePaymentEvent
    data class Action(val id: String) : NativeAlternativePaymentEvent
    data class DialogAction(val id: String, val isConfirmed: Boolean) : NativeAlternativePaymentEvent
    data class ActionConfirmationRequested(val id: String) : NativeAlternativePaymentEvent
    data class PermissionRequestResult(val permission: String, val isGranted: Boolean) : NativeAlternativePaymentEvent
    data class Dismiss(val failure: ProcessOutResult.Failure) : NativeAlternativePaymentEvent
}
