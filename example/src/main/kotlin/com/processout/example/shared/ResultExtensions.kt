package com.processout.example.shared

import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.rawValue
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodResult

fun PONativeAlternativePaymentMethodResult.Failure.toMessage() =
    "${javaClass.simpleName}: ${code.rawValue}: $message"

fun ProcessOutResult.Failure.toMessage() =
    "${javaClass.simpleName}: ${code.rawValue}: $message"
