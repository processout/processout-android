package com.processout.sdk.ui.web

internal interface WebAuthorizationDelegateCache {

    var delegate: WebAuthorizationDelegate?

    fun isCached(): Boolean

    fun remove(): WebAuthorizationDelegate?
}
