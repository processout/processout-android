package com.processout.sdk.checkout.threeds

import com.checkout.threeds.domain.model.Warning
import com.checkout.threeds.standalone.models.ConfigParameters
import com.checkout.threeds.standalone.models.ThreeDS2ServiceConfiguration
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.core.ProcessOutResult

interface POCheckout3DSServiceDelegate {

    /**
     * Notifies delegate that service is about to fingerprint device.
     * Implementation should create [ThreeDS2ServiceConfiguration] using [parameters] and return it.
     */
    fun configuration(parameters: ConfigParameters): ThreeDS2ServiceConfiguration

    /**
     *  Asks delegate whether service should continue with the given warnings.
     *  Default implementation ignores warnings and callbacks with _true_.
     */
    fun shouldContinue(
        warnings: Set<Warning>,
        callback: (Boolean) -> Unit
    ) = callback(true)

    /**
     * Asks delegate to handle 3DS redirect.
     */
    fun handle(
        redirect: PO3DSRedirect,
        callback: (ProcessOutResult<String>) -> Unit
    )
}
