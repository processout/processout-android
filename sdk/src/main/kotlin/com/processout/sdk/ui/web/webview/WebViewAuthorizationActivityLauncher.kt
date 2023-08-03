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

internal class WebViewAuthorizationActivityLauncher private constructor() {

    private lateinit var launcher: ActivityResultLauncher<WebViewConfiguration>
    private var activityOptions: ActivityOptionsCompat? = null

    companion object {
        fun create(
            from: Fragment,
            activityResultCallback: ActivityResultCallback<ProcessOutActivityResult<Uri>>
        ) = WebViewAuthorizationActivityLauncher().apply {
            launcher = from.registerForActivityResult(
                WebViewAuthorizationActivityContract(),
                activityResultCallback
            )
            activityOptions = from.context?.let { createActivityOptions(it) }
        }

        fun create(
            from: ComponentActivity,
            activityResultCallback: ActivityResultCallback<ProcessOutActivityResult<Uri>>
        ) = WebViewAuthorizationActivityLauncher().apply {
            launcher = from.registerForActivityResult(
                WebViewAuthorizationActivityContract(),
                from.activityResultRegistry,
                activityResultCallback
            )
            activityOptions = createActivityOptions(from)
        }

        private fun createActivityOptions(context: Context): ActivityOptionsCompat =
            ActivityOptionsCompat.makeCustomAnimation(
                context, R.anim.slide_in_right, R.anim.slide_out_left
            )
    }

    fun launch(configuration: WebViewConfiguration) {
        launcher.launch(configuration, activityOptions)
    }
}
