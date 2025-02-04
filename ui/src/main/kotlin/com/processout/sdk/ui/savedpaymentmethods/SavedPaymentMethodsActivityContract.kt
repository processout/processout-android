package com.processout.sdk.ui.savedpaymentmethods

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.POUnit
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.BuildConfig

internal class SavedPaymentMethodsActivityContract : ActivityResultContract
<POSavedPaymentMethodsConfiguration, ProcessOutActivityResult<POUnit>>() {

    companion object {
        const val EXTRA_CONFIGURATION = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_CONFIGURATION"
        const val EXTRA_RESULT = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_RESULT"
        const val EXTRA_FORCE_FINISH = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_FORCE_FINISH"
    }

    override fun createIntent(
        context: Context,
        input: POSavedPaymentMethodsConfiguration
    ) = Intent(context, SavedPaymentMethodsActivity::class.java)
        .putExtra(EXTRA_CONFIGURATION, input)

    @Suppress("DEPRECATION")
    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): ProcessOutActivityResult<POUnit> {
        intent?.setExtrasClassLoader(ProcessOutActivityResult::class.java.classLoader)
        return intent?.getParcelableExtra(EXTRA_RESULT)
            ?: ProcessOutActivityResult.Failure(
                code = POFailure.Code.Internal(),
                message = "Activity result was not provided."
            ).also { POLogger.error("%s", it) }
    }
}
