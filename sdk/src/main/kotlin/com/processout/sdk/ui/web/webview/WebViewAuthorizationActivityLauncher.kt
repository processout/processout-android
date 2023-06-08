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
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.web.WebAuthorizationDelegate

internal class WebViewAuthorizationActivityLauncher private constructor() {

    private lateinit var launcher: ActivityResultLauncher<WebViewConfiguration>
    private lateinit var delegate: WebAuthorizationDelegate
    private var activityOptions: ActivityOptionsCompat? = null

    companion object {
        fun create(from: Fragment) = WebViewAuthorizationActivityLauncher().apply {
            launcher = from.registerForActivityResult(
                WebViewAuthorizationActivityContract(),
                activityResultCallback
            )
            activityOptions = from.context?.let { createActivityOptions(it) }
        }

        fun create(from: ComponentActivity) = WebViewAuthorizationActivityLauncher().apply {
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

    fun launch(configuration: WebViewConfiguration, delegate: WebAuthorizationDelegate) {
        this.delegate = delegate
        launcher.launch(configuration, activityOptions)
    }

    private val activityResultCallback = ActivityResultCallback<ProcessOutActivityResult<Uri>> {
        when (it) {
            is ProcessOutActivityResult.Success -> delegate.complete(uri = it.value)
            is ProcessOutActivityResult.Failure -> delegate.complete(
                ProcessOutResult.Failure(it.code, it.message)
            )
        }
    }
}
