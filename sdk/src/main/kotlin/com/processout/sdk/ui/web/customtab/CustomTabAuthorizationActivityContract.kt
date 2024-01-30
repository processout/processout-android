package com.processout.sdk.ui.web.customtab

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.processout.sdk.BuildConfig
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.logger.POLogger

internal class CustomTabAuthorizationActivityContract : ActivityResultContract
<CustomTabConfiguration, ProcessOutActivityResult<Uri>>() {

    companion object {
        const val EXTRA_CONFIGURATION = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_CONFIGURATION"
        const val EXTRA_RESULT = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_RESULT"
        const val EXTRA_TIMEOUT_FINISH = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_TIMEOUT_FINISH"
    }

    override fun createIntent(
        context: Context,
        input: CustomTabConfiguration
    ) = Intent(context, POCustomTabAuthorizationActivity::class.java)
        .putExtra(EXTRA_CONFIGURATION, input)

    @Suppress("DEPRECATION")
    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): ProcessOutActivityResult<Uri> {
        intent?.setExtrasClassLoader(ProcessOutActivityResult::class.java.classLoader)
        return intent?.getParcelableExtra(EXTRA_RESULT)
            ?: ProcessOutActivityResult.Failure(
                code = POFailure.Code.Internal(),
                message = "Activity result was not provided."
            ).also { POLogger.error("%s", it) }
    }
}
