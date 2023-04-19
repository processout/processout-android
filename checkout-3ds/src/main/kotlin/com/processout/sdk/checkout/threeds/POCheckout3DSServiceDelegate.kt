package com.processout.sdk.checkout.threeds

import com.checkout.threeds.domain.model.Warning
import com.checkout.threeds.standalone.models.ConfigParameters
import com.checkout.threeds.standalone.models.ThreeDS2ServiceConfiguration
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.api.service.PO3DSResult

interface POCheckout3DSServiceDelegate {

    fun configuration(parameters: ConfigParameters): ThreeDS2ServiceConfiguration

    /**
     *  Asks delegate whether service should continue with the given warnings.
     *  Default implementation ignores warnings and callbacks with _true_.
     */
    fun shouldContinue(
        warnings: Set<Warning>,
        callback: (Boolean) -> Unit
    ) = callback(true)

    fun handle(
        redirect: PO3DSRedirect,
        callback: (PO3DSResult<String>) -> Unit
    )
}
