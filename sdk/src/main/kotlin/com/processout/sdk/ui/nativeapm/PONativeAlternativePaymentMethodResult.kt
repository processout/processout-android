package com.processout.sdk.ui.nativeapm

import android.os.Parcelable
import com.processout.sdk.core.POFailure
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodResult.Failure
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodResult.Success
import kotlinx.parcelize.Parcelize

/**
 * Provides [Success] or [Failure] as a result of native alternative payment.
 */
sealed class PONativeAlternativePaymentMethodResult : Parcelable {
    @Parcelize
    data object Success : PONativeAlternativePaymentMethodResult()

    @Parcelize
    data class Failure(
        val code: POFailure.Code,
        val message: String? = null,
        val invalidFields: List<POFailure.InvalidField>? = null
    ) : PONativeAlternativePaymentMethodResult()
}
