package com.processout.sdk.ui.web

import android.net.Uri
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess

internal object WebAuthorizationActivityResultDispatcher :
    ActivityResultDispatcher<Uri>, WebAuthorizationDelegateCache {

    override var delegate: WebAuthorizationDelegate? = null

    override fun dispatch(result: ProcessOutActivityResult<Uri>) {
        if (!isCached()) {
            POLogger.error("Cannot provide result. Delegate is not cached. Possibly process was killed.")
            return
        }
        result
            .onSuccess { remove()?.complete(uri = it) }
            .onFailure { remove()?.complete(ProcessOutResult.Failure(it.code, it.message)) }
    }

    override fun isCached() = delegate != null

    override fun remove(): WebAuthorizationDelegate? {
        val cached = delegate
        delegate = null
        return cached
    }
}
