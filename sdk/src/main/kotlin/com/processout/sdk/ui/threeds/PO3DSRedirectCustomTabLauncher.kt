package com.processout.sdk.ui.threeds

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.api.network.ApiConstants
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.web.DefaultWebAuthorizationDelegateCache
import com.processout.sdk.ui.web.WebAuthorizationDelegate
import com.processout.sdk.ui.web.WebAuthorizationDelegateCache
import com.processout.sdk.ui.web.customtab.CustomTabAuthorizationActivityContract
import com.processout.sdk.ui.web.customtab.CustomTabConfiguration
import com.processout.sdk.ui.web.webview.WebViewAuthorizationActivityLauncher
import com.processout.sdk.ui.web.webview.WebViewConfiguration

class PO3DSRedirectCustomTabLauncher private constructor(
    private val delegateCache: WebAuthorizationDelegateCache
) {

    private lateinit var customTabLauncher: ActivityResultLauncher<CustomTabConfiguration>
    private lateinit var webViewFallbackLauncher: WebViewAuthorizationActivityLauncher

    companion object {
        fun create(from: Fragment) = PO3DSRedirectCustomTabLauncher(
            DefaultWebAuthorizationDelegateCache
        ).apply {
            customTabLauncher = from.registerForActivityResult(
                CustomTabAuthorizationActivityContract(),
                activityResultCallback
            )
            webViewFallbackLauncher = WebViewAuthorizationActivityLauncher.create(
                from, activityResultCallback
            )
        }

        fun create(from: ComponentActivity) = PO3DSRedirectCustomTabLauncher(
            DefaultWebAuthorizationDelegateCache
        ).apply {
            customTabLauncher = from.registerForActivityResult(
                CustomTabAuthorizationActivityContract(),
                from.activityResultRegistry,
                activityResultCallback
            )
            webViewFallbackLauncher = WebViewAuthorizationActivityLauncher.create(
                from, activityResultCallback
            )
        }
    }

    fun launch(
        redirect: PO3DSRedirect,
        returnUrl: String,
        callback: (ProcessOutResult<String>) -> Unit
    ) {
        if (delegateCache.isCached()) {
            callback(
                ProcessOutResult.Failure(
                    POFailure.Code.Generic(),
                    "Launcher is already running."
                )
            )
            return
        }

        val delegate: WebAuthorizationDelegate = ThreeDSRedirectWebAuthorizationDelegate(
            Uri.parse(redirect.url.toString()),
            callback
        )
        delegateCache.delegate = delegate

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
                    returnUris = listOf(
                        Uri.parse(ApiConstants.CHECKOUT_RETURN_URL),
                        Uri.parse(returnUrl)
                    ),
                    sdkVersion = ProcessOut.VERSION,
                    timeoutSeconds = redirect.timeoutSeconds
                )
            )
        }
    }

    @Deprecated(
        message = "Use function launch(redirect, returnUrl, callback)",
        replaceWith = ReplaceWith("launch(redirect, returnUrl, callback)")
    )
    fun launch(redirect: PO3DSRedirect, callback: (ProcessOutResult<String>) -> Unit) {
        launch(redirect, returnUrl = String(), callback)
    }

    private val activityResultCallback = ActivityResultCallback<ProcessOutActivityResult<Uri>> {
        when (it) {
            is ProcessOutActivityResult.Success -> delegateCache.remove()?.complete(uri = it.value)
            is ProcessOutActivityResult.Failure -> delegateCache.remove()?.complete(
                ProcessOutResult.Failure(it.code, it.message)
            )
        }
    }
}
