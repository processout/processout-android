package com.processout.sdk.ui.napm

import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.core.ProcessOutResult

internal sealed interface NativeAlternativePaymentEvent {
    data class FieldValueChanged(val id: String, val value: TextFieldValue) : NativeAlternativePaymentEvent
    data class FieldFocusChanged(val id: String, val isFocused: Boolean) : NativeAlternativePaymentEvent
    data class Action(val id: String) : NativeAlternativePaymentEvent
    data class Dismiss(val failure: ProcessOutResult.Failure) : NativeAlternativePaymentEvent
}

internal sealed interface NativeAlternativePaymentCompletion {
    data object Awaiting : NativeAlternativePaymentCompletion
    data object Success : NativeAlternativePaymentCompletion
    data class Failure(val failure: ProcessOutResult.Failure) : NativeAlternativePaymentCompletion
}
