package com.processout.sdk.netcetera.threeds

import androidx.activity.ComponentActivity
import com.netcetera.threeds.sdk.api.security.Warning

interface PONetcetera3DS2ServiceDelegate {

    fun activity(): ComponentActivity?

    /**
     *  Asks delegate whether the service should continue with the given warnings.
     *  Default implementation ignores any warnings and returns _true_.
     */
    suspend fun shouldContinue(warnings: Set<Warning>): Boolean = true
}
