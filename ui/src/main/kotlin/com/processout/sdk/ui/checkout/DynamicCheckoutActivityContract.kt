package com.processout.sdk.ui.checkout

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.processout.sdk.core.POFailure.Code.Generic
import com.processout.sdk.core.POFailure.GenericCode
import com.processout.sdk.core.POUnit
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.BuildConfig

internal class DynamicCheckoutActivityContract : ActivityResultContract
<PODynamicCheckoutConfiguration, ProcessOutActivityResult<POUnit>>() {

    companion object {
        const val EXTRA_CONFIGURATION = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_CONFIGURATION"
        const val EXTRA_RESULT = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_RESULT"
    }

    override fun createIntent(
        context: Context,
        input: PODynamicCheckoutConfiguration
    ) = Intent(context, PODynamicCheckoutActivity::class.java)
        .putExtra(EXTRA_CONFIGURATION, input)

    @Suppress("DEPRECATION")
    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): ProcessOutActivityResult<POUnit> {
        intent?.setExtrasClassLoader(ProcessOutActivityResult::class.java.classLoader)
        return intent?.getParcelableExtra(EXTRA_RESULT)
            ?: ProcessOutActivityResult.Failure(
                code = Generic(GenericCode.mobileAppProcessKilled),
                message = "App process was killed."
            ).also { POLogger.warn("%s", it) }
    }
}
