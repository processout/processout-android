package com.processout.sdk.ui.web

import android.net.Uri
import com.processout.sdk.core.ProcessOutResult

internal interface WebAuthorizationDelegate {
    val uri: Uri
    fun complete(uri: Uri)
    fun complete(failure: ProcessOutResult.Failure)
}
