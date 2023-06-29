package com.processout.sdk.ui.threeds

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.api.network.ApiConstants
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.web.WebAuthorizationDelegate
import com.processout.sdk.ui.web.customtab.CustomTabAuthorizationActivityContract
import com.processout.sdk.ui.web.customtab.CustomTabConfiguration
import com.processout.sdk.ui.web.webview.WebViewAuthorizationActivityLauncher
import com.processout.sdk.ui.web.webview.WebViewConfiguration

class PO3DSRedirectCustomTabLauncher private constructor() {

    private lateinit var customTabLauncher: ActivityResultLauncher<CustomTabConfiguration>
    private lateinit var webViewFallbackLauncher: WebViewAuthorizationActivityLauncher
    private lateinit var delegate: WebAuthorizationDelegate

    companion object {
        fun create(from: Fragment) = PO3DSRedirectCustomTabLauncher().apply {
            customTabLauncher = from.registerForActivityResult(
                CustomTabAuthorizationActivityContract(),
                activityResultCallback
            )
            webViewFallbackLauncher = WebViewAuthorizationActivityLauncher.create(from)
        }

        fun create(from: ComponentActivity) = PO3DSRedirectCustomTabLauncher().apply {
            customTabLauncher = from.registerForActivityResult(
                CustomTabAuthorizationActivityContract(),
                from.activityResultRegistry,
                activityResultCallback
            )
            webViewFallbackLauncher = WebViewAuthorizationActivityLauncher.create(from)
        }
    }

    fun launch(redirect: PO3DSRedirect, callback: (ProcessOutResult<String>) -> Unit) {
        delegate = ThreeDSRedirectWebAuthorizationDelegate(
            redirect.url.let { Uri.parse(it.toString()) },
            callback
        )

        // TODO: Delete this ad-hoc when backend Chrome redirect issue is fixed.
        val forceWebView = true
        if (forceWebView) {
            webViewFallbackLauncher.launch(
                WebViewConfiguration(
                    uri = delegate.uri,
                    returnUris = listOf(Uri.parse(ApiConstants.CHECKOUT_URL)),
                    sdkVersion = ProcessOut.VERSION,
                    timeoutSeconds = redirect.timeoutSeconds
                ), delegate
            )
            return
        }

        if (ProcessOut.instance.browserCapabilities.isCustomTabsSupported()) {
            customTabLauncher.launch(
                CustomTabConfiguration(
                    uri = delegate.uri,
                    timeoutSeconds = redirect.timeoutSeconds
                )
            )
        } else {
            webViewFallbackLauncher.launch(
                WebViewConfiguration(
                    uri = delegate.uri,
                    returnUris = listOf(Uri.parse(ApiConstants.CHECKOUT_URL)),
                    sdkVersion = ProcessOut.VERSION,
                    timeoutSeconds = redirect.timeoutSeconds
                ), delegate
            )
        }
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
