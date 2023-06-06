package com.processout.sdk.ui.web

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.processout.sdk.BuildConfig
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutActivityResult

internal class WebViewAuthorizationActivityContract : ActivityResultContract
<WebViewConfiguration, ProcessOutActivityResult<Uri>>() {

    companion object {
        const val EXTRA_CONFIGURATION = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_CONFIGURATION"
        const val EXTRA_RESULT = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_RESULT"
    }

    override fun createIntent(
        context: Context,
        input: WebViewConfiguration
    ) = Intent(context, POWebViewAuthorizationActivity::class.java)
        .putExtra(EXTRA_CONFIGURATION, input)

    @Suppress("DEPRECATION")
    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): ProcessOutActivityResult<Uri> =
        intent?.getParcelableExtra(EXTRA_RESULT)
            ?: ProcessOutActivityResult.Failure(
                POFailure.Code.Internal(),
                "Activity result was not provided."
            )
}
