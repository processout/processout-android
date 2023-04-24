package com.processout.sdk.ui.threeds

import android.net.Uri
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.web.WebViewDelegate

internal class ThreeDSRedirectWebViewDelegate(
    override val uri: Uri,
    private val callback: ((ProcessOutResult<String>) -> Unit)
) : WebViewDelegate {

    private companion object {
        private const val TOKEN_QUERY_KEY = "token"
    }

    override fun complete(uri: Uri) {
        uri.getQueryParameter(TOKEN_QUERY_KEY)?.let { token ->
            callback(ProcessOutResult.Success(token))
        } ?: callback(
            ProcessOutResult.Failure(
                POFailure.Code.Internal(),
                "Token not found in URI: $uri"
            )
        )
    }

    override fun complete(failure: ProcessOutResult.Failure) {
        callback(failure.copy())
    }
}
