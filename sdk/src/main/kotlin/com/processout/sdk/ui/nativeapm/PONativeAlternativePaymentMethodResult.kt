package com.processout.sdk.ui.nativeapm

import android.os.Parcelable
import com.processout.sdk.api.network.exception.ProcessOutApiError
import kotlinx.parcelize.Parcelize

sealed class PONativeAlternativePaymentMethodResult : Parcelable {
    @Parcelize
    object Success : PONativeAlternativePaymentMethodResult()

    @Parcelize
    data class Failure(
        val message: String,
        val exceptionMessage: String? = null,
        val code: Int? = null,
        val apiError: ProcessOutApiError? = null
    ) : PONativeAlternativePaymentMethodResult()

    @Parcelize
    object Canceled : PONativeAlternativePaymentMethodResult()
}
