package com.processout.sdk.ui.card.tokenization

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.BuildConfig

internal class CardTokenizationActivityContract : ActivityResultContract
<POCardTokenizationConfiguration, ProcessOutActivityResult<POCard>>() {

    companion object {
        const val EXTRA_CONFIGURATION = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_CONFIGURATION"
        const val EXTRA_RESULT = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_RESULT"
    }

    override fun createIntent(
        context: Context,
        input: POCardTokenizationConfiguration
    ) = Intent(context, POCardTokenizationActivity::class.java)
        .putExtra(EXTRA_CONFIGURATION, input)

    @Suppress("DEPRECATION")
    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): ProcessOutActivityResult<POCard> {
        intent?.setExtrasClassLoader(ProcessOutActivityResult::class.java.classLoader)
        return intent?.getParcelableExtra(EXTRA_RESULT)
            ?: ProcessOutActivityResult.Failure(
                code = POFailure.Code.Internal(),
                message = "Activity result was not provided."
            ).also { POLogger.error("%s", it) }
    }
}
