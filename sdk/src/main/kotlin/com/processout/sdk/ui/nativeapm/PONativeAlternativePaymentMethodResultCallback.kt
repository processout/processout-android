package com.processout.sdk.ui.nativeapm

/**
 * Functional interface that provides result of native alternative payment as as callback.
 */
fun interface PONativeAlternativePaymentMethodResultCallback {
    fun onNativeAlternativePaymentMethodResult(result: PONativeAlternativePaymentMethodResult)
}
