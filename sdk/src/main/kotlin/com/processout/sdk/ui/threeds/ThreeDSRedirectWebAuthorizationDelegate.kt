package com.processout.sdk.ui.threeds

import android.net.Uri
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.web.WebAuthorizationDelegate

internal class ThreeDSRedirectWebAuthorizationDelegate(
    override val uri: Uri,
    private val callback: ((ProcessOutResult<String>) -> Unit)
) : WebAuthorizationDelegate {

    private companion object {
        private const val TOKEN_QUERY_KEY = "token"
    }

    override fun complete(uri: Uri) {
        if (uri.isHierarchical) {
            callback(
                ProcessOutResult.Success(
                    value = uri.getQueryParameter(TOKEN_QUERY_KEY) ?: String()
                )
            )
        } else {
            callback(
                ProcessOutResult.Failure(
                    code = POFailure.Code.Internal(),
                    message = "Invalid or malformed 3DS redirect URI: $uri"
                )
            )
        }
    }

    override fun complete(failure: ProcessOutResult.Failure) {
        callback(failure)
    }
}
