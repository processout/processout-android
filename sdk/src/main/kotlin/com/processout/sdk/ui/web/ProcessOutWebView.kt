package com.processout.sdk.ui.web

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.webkit.*
import androidx.annotation.RequiresApi
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import java.util.concurrent.TimeUnit

@SuppressLint("ViewConstructor", "SetJavaScriptEnabled")
internal class ProcessOutWebView(
    context: Context,
    private val configuration: Configuration,
    private val delegate: WebViewDelegate?
) : WebView(context) {

    internal data class Configuration(
        val returnUris: List<Uri>,
        val sdkVersion: String,
        val timeoutSeconds: Int?
    )

    private companion object {
        private const val USER_AGENT_PREFIX = "ProcessOut Android-WebView/"
        private const val RETURN_URL_PATH_PREFIX = "/helpers/mobile-processout-webview-landing"
    }

    private val timeoutHandler by lazy { Handler(Looper.getMainLooper()) }

    init {
        setup()
        load()
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

    private fun load() {
        delegate?.uri?.let { loadUrl(it.toString()) }
        configuration.timeoutSeconds?.let {
            timeoutHandler.postDelayed(
                { complete(ProcessOutResult.Failure(POFailure.Code.Timeout())) },
                TimeUnit.SECONDS.toMillis(it.toLong())
            )
        }
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
                        }?.let { complete(uri) }
                    }
                }
            }

            override fun onReceivedSslError(
                view: WebView?, handler: SslErrorHandler?, error: SslError?
            ) {
                handler?.cancel()
                complete(
                    ProcessOutResult.Failure(
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
                        ProcessOutResult.Failure(
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
                    ProcessOutResult.Failure(
                        POFailure.Code.Generic(),
                        "$description | Failed to load URL: $failingUrl"
                    )
                )
            }
        } ?: complete(
            ProcessOutResult.Failure(
                POFailure.Code.Internal(),
                "$description | Failed to load URL: $failingUrl"
            )
        )
    }

    private fun complete(uri: Uri) {
        timeoutHandler.removeCallbacksAndMessages(null)
        delegate?.complete(uri)
    }

    private fun complete(failure: ProcessOutResult.Failure) {
        timeoutHandler.removeCallbacksAndMessages(null)
        delegate?.complete(failure)
    }
}
