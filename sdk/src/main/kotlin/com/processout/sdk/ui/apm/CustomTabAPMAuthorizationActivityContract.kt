package com.processout.sdk.ui.apm

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.processout.sdk.BuildConfig
import com.processout.sdk.api.model.request.POAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.ui.web.POCustomTabAuthorizationActivity

internal class CustomTabAPMAuthorizationActivityContract : ActivityResultContract
<POAlternativePaymentMethodRequest, ProcessOutActivityResult<POAlternativePaymentMethodResponse>>() {

    companion object {
        const val EXTRA_CONFIGURATION = "${BuildConfig.LIBRARY_PACKAGE_NAME}.apm.EXTRA_CONFIGURATION"
        const val EXTRA_RESULT = "${BuildConfig.LIBRARY_PACKAGE_NAME}.apm.EXTRA_RESULT"
    }

    override fun createIntent(context: Context, input: POAlternativePaymentMethodRequest) =
        Intent(context, POCustomTabAuthorizationActivity::class.java)
            .putExtra(EXTRA_CONFIGURATION, input)

    @Suppress("DEPRECATION")
    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): ProcessOutActivityResult<POAlternativePaymentMethodResponse> =
        intent?.getParcelableExtra(EXTRA_RESULT)
            ?: ProcessOutActivityResult.Failure(
                POFailure.Code.Internal(),
                "Activity result was not provided."
            )
}
