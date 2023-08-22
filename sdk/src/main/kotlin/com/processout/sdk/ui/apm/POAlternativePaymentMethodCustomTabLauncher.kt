@file:Suppress("MoveVariableDeclarationIntoWhen")

package com.processout.sdk.ui.apm

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.api.network.ApiConstants
import com.processout.sdk.api.service.POAlternativePaymentMethodsService
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.web.DefaultWebAuthorizationDelegateCache
import com.processout.sdk.ui.web.WebAuthorizationDelegate
import com.processout.sdk.ui.web.WebAuthorizationDelegateCache
import com.processout.sdk.ui.web.customtab.CustomTabAuthorizationActivityContract
import com.processout.sdk.ui.web.customtab.CustomTabConfiguration
import com.processout.sdk.ui.web.webview.WebViewAuthorizationActivityLauncher
import com.processout.sdk.ui.web.webview.WebViewConfiguration

class POAlternativePaymentMethodCustomTabLauncher private constructor(
    private val alternativePaymentMethods: POAlternativePaymentMethodsService,
    private val delegateCache: WebAuthorizationDelegateCache
) {

    private lateinit var customTabLauncher: ActivityResultLauncher<CustomTabConfiguration>
    private lateinit var webViewFallbackLauncher: WebViewAuthorizationActivityLauncher

    companion object {
        /**
         * When launcher created with this method use
         * __launch(request, returnUrl)__ or __launch(uri, returnUrl)__.
         */
        fun create(
            from: Fragment,
            callback: (ProcessOutResult<POAlternativePaymentMethodResponse>) -> Unit
        ) = POAlternativePaymentMethodCustomTabLauncher(
            ProcessOut.instance.alternativePaymentMethods,
            DefaultWebAuthorizationDelegateCache
        ).apply {
            val activityResultHandler = ActivityResultHandler(alternativePaymentMethods, callback)
            customTabLauncher = from.registerForActivityResult(
                CustomTabAuthorizationActivityContract(),
                activityResultHandler
            )
            webViewFallbackLauncher = WebViewAuthorizationActivityLauncher.create(
                from, activityResultHandler
            )
        }

        /**
         * When launcher created with this method use
         * __launch(request, returnUrl)__ or __launch(uri, returnUrl)__.
         */
        fun create(
            from: ComponentActivity,
            callback: (ProcessOutResult<POAlternativePaymentMethodResponse>) -> Unit
        ) = POAlternativePaymentMethodCustomTabLauncher(
            ProcessOut.instance.alternativePaymentMethods,
            DefaultWebAuthorizationDelegateCache
        ).apply {
            val activityResultHandler = ActivityResultHandler(alternativePaymentMethods, callback)
            customTabLauncher = from.registerForActivityResult(
                CustomTabAuthorizationActivityContract(),
                from.activityResultRegistry,
                activityResultHandler
            )
            webViewFallbackLauncher = WebViewAuthorizationActivityLauncher.create(
                from, activityResultHandler
            )
        }

        @Deprecated(
            message = "Use function create(from, callback)",
            replaceWith = ReplaceWith("create(from, callback)")
        )
        fun create(from: Fragment) = POAlternativePaymentMethodCustomTabLauncher(
            ProcessOut.instance.alternativePaymentMethods,
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

        @Deprecated(
            message = "Use function create(from, callback)",
            replaceWith = ReplaceWith("create(from, callback)")
        )
        fun create(from: ComponentActivity) = POAlternativePaymentMethodCustomTabLauncher(
            ProcessOut.instance.alternativePaymentMethods,
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

    /**
     * Use when launcher created with method __create(from, callback)__.
     */
    fun launch(
        request: POAlternativePaymentMethodRequest,
        returnUrl: String
    ) {
        val uri = when (val result = alternativePaymentMethods.alternativePaymentMethodUri(request)) {
            is ProcessOutResult.Success -> result.value
            is ProcessOutResult.Failure -> Uri.EMPTY
        }
        launch(uri, returnUrl)
    }

    /**
     * Use when launcher created with method __create(from, callback)__.
     */
    fun launch(uri: Uri, returnUrl: String) {
        if (ProcessOut.instance.browserCapabilities.isCustomTabsSupported()) {
            customTabLauncher.launch(
                CustomTabConfiguration(
                    uri = uri,
                    timeoutSeconds = null
                )
            )
        } else {
            POLogger.debug("Custom Chrome Tabs is not supported on device. Will use WebView.")
            webViewFallbackLauncher.launch(
                WebViewConfiguration(
                    uri = uri,
                    returnUris = listOf(
                        Uri.parse(ApiConstants.CHECKOUT_RETURN_URL),
                        Uri.parse(returnUrl)
                    ),
                    sdkVersion = ProcessOut.VERSION,
                    timeoutSeconds = null
                )
            )
        }
    }

    @Deprecated(
        message = "Use function launch(request, returnUrl)",
        replaceWith = ReplaceWith("launch(request, returnUrl)")
    )
    fun launch(
        request: POAlternativePaymentMethodRequest,
        returnUrl: String,
        callback: (ProcessOutResult<POAlternativePaymentMethodResponse>) -> Unit
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

        val delegate: WebAuthorizationDelegate = AlternativePaymentMethodWebAuthorizationDelegate(
            ProcessOut.instance.alternativePaymentMethods,
            request, callback
        )
        delegateCache.delegate = delegate
        launch(delegate.uri, returnUrl)
    }

    @Deprecated(
        message = "Use function launch(request, returnUrl)",
        replaceWith = ReplaceWith("launch(request, returnUrl)")
    )
    fun launch(
        request: POAlternativePaymentMethodRequest,
        callback: (ProcessOutResult<POAlternativePaymentMethodResponse>) -> Unit
    ) {
        launch(request, returnUrl = String(), callback)
    }

    @Deprecated("Used in other deprecated methods.")
    private val activityResultCallback = ActivityResultCallback<ProcessOutActivityResult<Uri>> {
        if (delegateCache.isCached().not()) {
            POLogger.error("Cannot provide APM result. Delegate is not cached.")
        }
        when (it) {
            is ProcessOutActivityResult.Success -> delegateCache.remove()?.complete(uri = it.value)
            is ProcessOutActivityResult.Failure -> delegateCache.remove()?.complete(
                ProcessOutResult.Failure(it.code, it.message)
            )
        }
    }

    private class ActivityResultHandler(
        private val alternativePaymentMethods: POAlternativePaymentMethodsService,
        private val callback: (ProcessOutResult<POAlternativePaymentMethodResponse>) -> Unit
    ) : ActivityResultCallback<ProcessOutActivityResult<Uri>> {

        override fun onActivityResult(result: ProcessOutActivityResult<Uri>) {
            when (result) {
                is ProcessOutActivityResult.Success -> {
                    val serviceResult = alternativePaymentMethods.alternativePaymentMethodResponse(uri = result.value)
                    when (serviceResult) {
                        is ProcessOutResult.Success -> callback(serviceResult.copy())
                        is ProcessOutResult.Failure -> callback(serviceResult.copy())
                    }
                }
                is ProcessOutActivityResult.Failure -> callback(
                    ProcessOutResult.Failure(result.code, result.message)
                )
            }
        }
    }
}
