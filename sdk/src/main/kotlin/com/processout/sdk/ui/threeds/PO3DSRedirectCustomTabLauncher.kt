package com.processout.sdk.ui.threeds

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.api.network.ApiConstants
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.web.POActivityResultApi
import com.processout.sdk.ui.web.WebAuthorizationActivityResultDispatcher
import com.processout.sdk.ui.web.WebAuthorizationDelegate
import com.processout.sdk.ui.web.WebAuthorizationDelegateCache
import com.processout.sdk.ui.web.customtab.POCustomTabAuthorizationActivityContract
import com.processout.sdk.ui.web.customtab.POCustomTabConfiguration
import com.processout.sdk.ui.web.webview.POWebViewConfiguration
import com.processout.sdk.ui.web.webview.WebViewAuthorizationActivityLauncher

/**
 * Launcher that starts [POCustomTabAuthorizationActivity][com.processout.sdk.ui.web.customtab.POCustomTabAuthorizationActivity]
 * to handle 3DS and provide the result. If Custom Chrome Tabs is not available on the device it will fallback to the
 * [POWebViewAuthorizationActivity][com.processout.sdk.ui.web.webview.POWebViewAuthorizationActivity].
 */
class PO3DSRedirectCustomTabLauncher private constructor(
    private val delegateCache: WebAuthorizationDelegateCache
) {

    private lateinit var contract: POCustomTabAuthorizationActivityContract
    private lateinit var webViewFallbackLauncher: WebViewAuthorizationActivityLauncher

    companion object {
        /**
         * Creates the launcher from Fragment.
         */
        fun create(from: Fragment) = PO3DSRedirectCustomTabLauncher(
            WebAuthorizationActivityResultDispatcher
        ).apply {
            contract = POCustomTabAuthorizationActivityContract(from.requireActivity())
            webViewFallbackLauncher = WebViewAuthorizationActivityLauncher.create(
                from, activityResultCallback = null
            )
        }

        /**
         * Creates the launcher from Activity.
         */
        fun create(from: ComponentActivity) = PO3DSRedirectCustomTabLauncher(
            WebAuthorizationActivityResultDispatcher
        ).apply {
            contract = POCustomTabAuthorizationActivityContract(from)
            webViewFallbackLauncher = WebViewAuthorizationActivityLauncher.create(
                from, activityResultCallback = null
            )
        }
    }

    /**
     * Launches the activity.
     */
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
                ).also { POLogger.debug("%s", it) }
            )
            return
        }

        val delegate: WebAuthorizationDelegate = ThreeDSRedirectWebAuthorizationDelegate(
            Uri.parse(redirect.url.toString()),
            callback
        )
        delegateCache.delegate = delegate

        if (ProcessOut.instance.browserCapabilities.isCustomTabsSupported()) {
            contract.startActivity(
                POCustomTabConfiguration(
                    uri = delegate.uri,
                    returnUri = Uri.parse(returnUrl),
                    timeoutSeconds = redirect.timeoutSeconds,
                    resultApi = POActivityResultApi.Dispatcher
                )
            )
        } else {
            POLogger.info("Custom Chrome Tabs is not supported on device. Will use WebView.")
            webViewFallbackLauncher.startActivity(
                POWebViewConfiguration(
                    uri = delegate.uri,
                    returnUris = listOf(
                        Uri.parse(ApiConstants.CHECKOUT_RETURN_URL),
                        Uri.parse(returnUrl)
                    ),
                    sdkVersion = ProcessOut.VERSION,
                    timeoutSeconds = redirect.timeoutSeconds,
                    resultApi = POActivityResultApi.Dispatcher
                )
            )
        }
    }

    /**
     * Launches the activity.
     */
    @Deprecated(
        message = "Use function launch(redirect, returnUrl, callback)",
        replaceWith = ReplaceWith("launch(redirect, returnUrl, callback)")
    )
    fun launch(redirect: PO3DSRedirect, callback: (ProcessOutResult<String>) -> Unit) {
        launch(redirect, returnUrl = String(), callback)
    }
}
