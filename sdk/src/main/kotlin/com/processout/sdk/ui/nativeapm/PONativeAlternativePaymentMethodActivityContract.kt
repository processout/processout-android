package com.processout.sdk.ui.nativeapm

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.processout.sdk.BuildConfig
import com.processout.sdk.core.POFailure

class PONativeAlternativePaymentMethodActivityContract : ActivityResultContract
<PONativeAlternativePaymentMethodConfiguration, PONativeAlternativePaymentMethodResult>() {

    companion object {
        const val EXTRA_CONFIGURATION = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_CONFIGURATION"
        const val EXTRA_RESULT = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_RESULT"
    }

    override fun createIntent(
        context: Context,
        input: PONativeAlternativePaymentMethodConfiguration
    ) = Intent(context, PONativeAlternativePaymentMethodActivity::class.java)
        .putExtra(EXTRA_CONFIGURATION, input)

    @Suppress("DEPRECATION")
    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): PONativeAlternativePaymentMethodResult =
        intent?.getParcelableExtra(EXTRA_RESULT)
            ?: PONativeAlternativePaymentMethodResult.Failure(
                "Activity result was not provided.",
                POFailure.Code.Internal
            )
}
