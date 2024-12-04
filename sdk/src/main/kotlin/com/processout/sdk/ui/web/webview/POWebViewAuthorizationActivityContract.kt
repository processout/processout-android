package com.processout.sdk.ui.web.webview

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import com.processout.sdk.BuildConfig
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.processout.sdk.core.logger.POLogger

/** @suppress */
@ProcessOutInternalApi
class POWebViewAuthorizationActivityContract(
    private val activity: ComponentActivity
) : ActivityResultContract<POWebViewConfiguration, ProcessOutActivityResult<Uri>>() {

    companion object {
        const val EXTRA_CONFIGURATION = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_CONFIGURATION"
        const val EXTRA_RESULT = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_RESULT"
        const val EXTRA_FORCE_FINISH = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_FORCE_FINISH"
    }

    fun startActivity(
        configuration: POWebViewConfiguration,
        activityOptions: ActivityOptionsCompat
    ) {
        activity.startActivity(
            createIntent(activity, configuration),
            activityOptions.toBundle()
        )
    }

    override fun createIntent(
        context: Context,
        input: POWebViewConfiguration
    ) = Intent(context, POWebViewAuthorizationActivity::class.java)
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
