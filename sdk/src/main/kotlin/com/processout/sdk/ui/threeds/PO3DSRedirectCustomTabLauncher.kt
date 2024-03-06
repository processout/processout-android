package com.processout.sdk.ui.threeds

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.api.network.ApiConstants
import com.processout.sdk.core.*
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.web.ActivityResultApi
import com.processout.sdk.ui.web.WebAuthorizationActivityResultDispatcher
import com.processout.sdk.ui.web.WebAuthorizationDelegate
import com.processout.sdk.ui.web.WebAuthorizationDelegateCache
import com.processout.sdk.ui.web.customtab.CustomTabAuthorizationActivityContract
import com.processout.sdk.ui.web.customtab.CustomTabConfiguration
import com.processout.sdk.ui.web.webview.WebViewAuthorizationActivityLauncher
import com.processout.sdk.ui.web.webview.WebViewConfiguration

/**
 * Launcher that starts [POCustomTabAuthorizationActivity][com.processout.sdk.ui.web.customtab.POCustomTabAuthorizationActivity]
 * to handle 3DS and provide the result. If Custom Chrome Tabs is not available on the device it will fallback to the
 * [POWebViewAuthorizationActivity][com.processout.sdk.ui.web.webview.POWebViewAuthorizationActivity].
 */
class PO3DSRedirectCustomTabLauncher private constructor(
    private val delegateCache: WebAuthorizationDelegateCache
) {

    private lateinit var customTabLauncher: ActivityResultLauncher<CustomTabConfiguration>
    private lateinit var webViewFallbackLauncher: WebViewAuthorizationActivityLauncher

    companion object {
        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(from: Fragment) = PO3DSRedirectCustomTabLauncher(
            WebAuthorizationActivityResultDispatcher
        ).apply {
            customTabLauncher = from.registerForActivityResult(
                CustomTabAuthorizationActivityContract(),
                activityResultCallback
            )
            webViewFallbackLauncher = WebViewAuthorizationActivityLauncher.create(
                from, activityResultCallback
            )
        }

        /**
         * Creates the launcher from Activity.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(from: ComponentActivity) = PO3DSRedirectCustomTabLauncher(
            WebAuthorizationActivityResultDispatcher
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
            customTabLauncher.launch(
                CustomTabConfiguration(
                    uri = delegate.uri,
                    returnUri = Uri.parse(returnUrl),
                    timeoutSeconds = redirect.timeoutSeconds,
                    resultApi = ActivityResultApi.Dispatcher
                )
            )
        } else {
            POLogger.info("Custom Chrome Tabs is not supported on device. Will use WebView.")
            webViewFallbackLauncher.launch(
                WebViewConfiguration(
                    uri = delegate.uri,
                    returnUris = listOf(
                        Uri.parse(ApiConstants.CHECKOUT_RETURN_URL),
                        Uri.parse(returnUrl)
                    ),
                    sdkVersion = ProcessOut.VERSION,
                    timeoutSeconds = redirect.timeoutSeconds,
                    resultApi = ActivityResultApi.Dispatcher
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

    private val activityResultCallback = ActivityResultCallback<ProcessOutActivityResult<Uri>> { result ->
        if (!delegateCache.isCached()) {
            POLogger.error("Cannot provide result. Delegate is not cached. Possibly process was killed.")
            return@ActivityResultCallback
        }
        result
            .onSuccess { delegateCache.remove()?.complete(uri = it) }
            .onFailure { delegateCache.remove()?.complete(ProcessOutResult.Failure(it.code, it.message)) }
    }
}
