package com.processout.sdk.ui.web

internal object DefaultWebAuthorizationDelegateCache : WebAuthorizationDelegateCache {

    override var delegate: WebAuthorizationDelegate? = null

    override fun isCached() = delegate != null

    override fun remove(): WebAuthorizationDelegate? {
        val cached = delegate
        clear()
        return cached
    }

    override fun clear() {
        delegate = null
    }
}
