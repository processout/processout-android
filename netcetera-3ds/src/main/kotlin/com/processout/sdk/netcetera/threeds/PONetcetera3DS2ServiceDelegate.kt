package com.processout.sdk.netcetera.threeds

import androidx.activity.ComponentActivity
import com.netcetera.threeds.sdk.api.security.Warning
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.core.ProcessOutResult

/**
 * A delegate that handles communication with the [PONetcetera3DS2Service].
 */
interface PONetcetera3DS2ServiceDelegate {

    /**
     * Asks the delegate to provide an activity instance.
     * If you have a custom logic that handles activity lifecycle,
     * you may return _null_ to cleanup the service and stop the flow safely when activity has been destroyed.
     */
    fun activity(): ComponentActivity?

    /**
     * Asks the delegate to handle 3DS redirect.
     */
    fun handle(
        redirect: PO3DSRedirect,
        callback: (ProcessOutResult<String>) -> Unit
    )

    /**
     * Asks the delegate whether the service should continue with the given warnings.
     * Default implementation ignores any warnings and returns _true_.
     */
    suspend fun shouldContinue(warnings: Set<Warning>): Boolean = true
}
