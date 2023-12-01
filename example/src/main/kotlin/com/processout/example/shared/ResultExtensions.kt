package com.processout.example.shared

import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.rawValue
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodResult

fun ProcessOutResult.Failure.toMessage() =
    "${javaClass.simpleName}: ${code.rawValue}: $message"

fun ProcessOutActivityResult.Failure.toMessage() =
    "${javaClass.simpleName}: ${code.rawValue}: $message"

fun PONativeAlternativePaymentMethodResult.Failure.toMessage() =
    "${javaClass.simpleName}: ${code.rawValue}: $message"
