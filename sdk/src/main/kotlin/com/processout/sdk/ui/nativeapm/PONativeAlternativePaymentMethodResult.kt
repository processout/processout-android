package com.processout.sdk.ui.nativeapm

import android.os.Parcelable
import com.processout.sdk.core.POFailure
import kotlinx.parcelize.Parcelize

sealed class PONativeAlternativePaymentMethodResult : Parcelable {
    @Parcelize
    object Success : PONativeAlternativePaymentMethodResult()

    @Parcelize
    data class Failure(
        val message: String,
        val code: POFailure.Code,
        val invalidFields: List<POFailure.InvalidField>? = null
    ) : PONativeAlternativePaymentMethodResult()
}
