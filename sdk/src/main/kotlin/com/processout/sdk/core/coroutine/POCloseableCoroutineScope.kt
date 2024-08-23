package com.processout.sdk.core.coroutine

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

/** @suppress */
@ProcessOutInternalApi
class POCloseableCoroutineScope(
    override val coroutineContext: CoroutineContext
) : Closeable, CoroutineScope {

    override fun close() = coroutineContext.cancel()
}
