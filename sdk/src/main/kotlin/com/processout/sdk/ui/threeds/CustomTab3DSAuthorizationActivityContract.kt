package com.processout.sdk.ui.threeds

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.processout.sdk.BuildConfig
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.ui.web.POCustomTabAuthorizationActivity

internal class CustomTab3DSAuthorizationActivityContract : ActivityResultContract
<PO3DSRedirect, ProcessOutActivityResult<ThreeDSToken>>() {

    companion object {
        const val EXTRA_CONFIGURATION = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_CONFIGURATION"
        const val EXTRA_RESULT = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_RESULT"
    }

    override fun createIntent(context: Context, input: PO3DSRedirect) =
        Intent(context, POCustomTabAuthorizationActivity::class.java)
            .putExtra(EXTRA_CONFIGURATION, input)

    @Suppress("DEPRECATION")
    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): ProcessOutActivityResult<ThreeDSToken> =
        intent?.getParcelableExtra(EXTRA_RESULT)
            ?: ProcessOutActivityResult.Failure(
                POFailure.Code.Internal(),
                "Activity result was not provided."
            )
}
