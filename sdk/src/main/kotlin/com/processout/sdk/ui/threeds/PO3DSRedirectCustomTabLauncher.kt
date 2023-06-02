package com.processout.sdk.ui.threeds

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.web.CustomTabAuthorizationActivityContract
import com.processout.sdk.ui.web.CustomTabAuthorizationConfiguration
import com.processout.sdk.ui.web.WebAuthorizationDelegate

class PO3DSRedirectCustomTabLauncher private constructor() {

    private lateinit var launcher: ActivityResultLauncher<CustomTabAuthorizationConfiguration>
    private lateinit var delegate: WebAuthorizationDelegate

    companion object {
        fun create(from: Fragment) = PO3DSRedirectCustomTabLauncher().apply {
            launcher = from.registerForActivityResult(
                CustomTabAuthorizationActivityContract(),
                activityResultCallback
            )
        }

        fun create(from: ComponentActivity) = PO3DSRedirectCustomTabLauncher().apply {
            launcher = from.registerForActivityResult(
                CustomTabAuthorizationActivityContract(),
                from.activityResultRegistry,
                activityResultCallback
            )
        }
    }

    fun launch(redirect: PO3DSRedirect, callback: (ProcessOutResult<String>) -> Unit) {
        delegate = ThreeDSRedirectWebAuthorizationDelegate(
            redirect.url.let { Uri.parse(it.toString()) },
            callback
        )
        launcher.launch(
            CustomTabAuthorizationConfiguration(
                uri = delegate.uri,
                timeoutSeconds = redirect.timeoutSeconds
            )
        )
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
