package com.processout.sdk.ui.threeds

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.webkit.*
import androidx.annotation.RequiresApi
import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.model.response.PO3DSRedirect
import com.processout.sdk.api.network.ApiConstants
import com.processout.sdk.api.service.PO3DSResult
import com.processout.sdk.core.POFailure
import java.util.concurrent.TimeUnit

@SuppressLint("ViewConstructor", "SetJavaScriptEnabled")
class PO3DSWebView private constructor(
    context: Context,
    private val configuration: Configuration,
    private val callback: (PO3DSResult<String>) -> Unit
) : WebView(context) {

    private companion object {
        private const val USER_AGENT_PREFIX = "ProcessOut Android-WebView/"
        private const val RETURN_URL_PATH_PREFIX = "/helpers/mobile-processout-webview-landing"
        private const val TOKEN_QUERY_KEY = "token"
    }

    private val timeoutHandler by lazy { Handler(Looper.getMainLooper()) }

    init {
        setup()
        loadUrl(configuration.uri.toString())
        configuration.timeoutSeconds?.let {
            timeoutHandler.postDelayed(
                { complete(PO3DSResult.Failure(POFailure.Code.Timeout())) },
                TimeUnit.SECONDS.toMillis(it.toLong())
            )
        }
    }

    private fun setup() {
        with(settings) {
            userAgentString = USER_AGENT_PREFIX + configuration.sdkVersion
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
        }
        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
        setClient()
    }

    private fun setClient() {
        webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                url?.let {
                    val uri = Uri.parse(it)
                    if (uri.isHierarchical && uri.path != null &&
                        uri.path!!.startsWith(RETURN_URL_PATH_PREFIX)
                    ) {
                        configuration.returnUris.find { returnUri ->
                            uri.scheme == returnUri.scheme && uri.host == returnUri.host
                        }?.let {
                            val token = uri.getQueryParameter(TOKEN_QUERY_KEY)
                            if (token != null)
                                complete(PO3DSResult.Success(token))
                            else complete(
                                PO3DSResult.Failure(
                                    POFailure.Code.Internal(),
                                    "Token not found in URL: $url"
                                )
                            )
                        }
                    }
                }
            }

            override fun onReceivedSslError(
                view: WebView?, handler: SslErrorHandler?, error: SslError?
            ) {
                handler?.cancel()
                complete(
                    PO3DSResult.Failure(
                        POFailure.Code.Generic(),
                        "SSL error: ${error?.toString()}"
                    )
                )
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceivedHttpError(
                view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?
            ) {
                completeFailedWebRequest(
                    request = request,
                    description = errorResponse?.statusCode.toString()
                )
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceivedError(
                view: WebView?, request: WebResourceRequest?, error: WebResourceError?
            ) {
                completeFailedWebRequest(
                    request = request,
                    description = error?.description.toString()
                )
            }

            @Suppress("OVERRIDE_DEPRECATION")
            override fun onReceivedError(
                view: WebView?, errorCode: Int, description: String?, failingUrl: String?
            ) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    complete(
                        PO3DSResult.Failure(
                            POFailure.Code.Generic(),
                            "$description | Failed to load URL: $failingUrl"
                        )
                    )
                }
            }
        }
    }

    private fun completeFailedWebRequest(request: WebResourceRequest?, description: String?) {
        val failingUrl = request?.url.toString()
        request?.let {
            if (it.isForMainFrame) {
                complete(
                    PO3DSResult.Failure(
                        POFailure.Code.Generic(),
                        "$description | Failed to load URL: $failingUrl"
                    )
                )
            }
        } ?: complete(
            PO3DSResult.Failure(
                POFailure.Code.Internal(),
                "$description | Failed to load URL: $failingUrl"
            )
        )
    }

    private fun complete(result: PO3DSResult<String>) {
        timeoutHandler.removeCallbacksAndMessages(null)
        callback(result)
    }

    private data class Configuration(
        val uri: Uri,
        val returnUris: List<Uri>,
        val sdkVersion: String,
        val timeoutSeconds: Int?
    )

    data class Builder(
        val activity: Activity,
        val redirect: PO3DSRedirect,
        val callback: (PO3DSResult<String>) -> Unit
    ) {
        fun build(): WebView = PO3DSWebView(
            activity,
            Configuration(
                uri = Uri.parse(redirect.url.toString()),
                returnUris = listOf(Uri.parse(ApiConstants.CHECKOUT_URL)),
                sdkVersion = ProcessOutApi.VERSION,
                timeoutSeconds = redirect.timeoutSeconds
            ),
            callback
        )
    }
}
