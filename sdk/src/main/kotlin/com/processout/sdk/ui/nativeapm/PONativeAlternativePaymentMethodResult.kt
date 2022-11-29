package com.processout.sdk.ui.nativeapm

import android.os.Parcelable
import com.processout.sdk.core.exception.ProcessOutException
import kotlinx.parcelize.Parcelize

sealed class PONativeAlternativePaymentMethodResult : Parcelable {
    @Parcelize
    object Success : PONativeAlternativePaymentMethodResult()

    @Parcelize
    data class Failure(
        val exception: ProcessOutException
    ) : PONativeAlternativePaymentMethodResult()

    @Parcelize
    object Canceled : PONativeAlternativePaymentMethodResult()
}
