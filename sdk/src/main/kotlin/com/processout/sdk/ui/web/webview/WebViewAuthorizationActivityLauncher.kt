package com.processout.sdk.ui.web.webview

import android.content.Context
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.processout.sdk.R
import com.processout.sdk.core.ProcessOutActivityResult

internal class WebViewAuthorizationActivityLauncher private constructor(
    private val contract: WebViewAuthorizationActivityContract,
    private val launcher: ActivityResultLauncher<WebViewConfiguration>?,
    private val activityOptions: ActivityOptionsCompat
) {

    companion object {
        fun create(
            from: Fragment,
            activityResultCallback: ActivityResultCallback<ProcessOutActivityResult<Uri>>?
        ): WebViewAuthorizationActivityLauncher {
            val contract = WebViewAuthorizationActivityContract(from.requireActivity())
            return WebViewAuthorizationActivityLauncher(
                contract = contract,
                launcher = activityResultCallback?.let { callback ->
                    from.registerForActivityResult(contract, callback)
                },
                activityOptions = createActivityOptions(from.requireContext())
            )
        }

        fun create(
            from: ComponentActivity,
            activityResultCallback: ActivityResultCallback<ProcessOutActivityResult<Uri>>?
        ): WebViewAuthorizationActivityLauncher {
            val contract = WebViewAuthorizationActivityContract(from)
            return WebViewAuthorizationActivityLauncher(
                contract = contract,
                launcher = activityResultCallback?.let { callback ->
                    from.registerForActivityResult(
                        contract, from.activityResultRegistry, callback
                    )
                },
                activityOptions = createActivityOptions(from)
            )
        }

        private fun createActivityOptions(context: Context): ActivityOptionsCompat =
            ActivityOptionsCompat.makeCustomAnimation(
                context, R.anim.po_slide_in_right, R.anim.po_slide_out_left
            )
    }

    fun startActivity(configuration: WebViewConfiguration) {
        contract.startActivity(configuration, activityOptions)
    }

    fun launch(configuration: WebViewConfiguration) {
        launcher?.launch(configuration, activityOptions)
    }
}
