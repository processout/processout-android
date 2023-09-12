package com.processout.sdk.checkout.threeds

import com.checkout.threeds.domain.model.Warning
import com.checkout.threeds.standalone.models.ConfigParameters
import com.checkout.threeds.standalone.models.ThreeDS2ServiceConfiguration
import com.processout.sdk.api.model.threeds.PO3DS2AuthenticationRequest
import com.processout.sdk.api.model.threeds.PO3DS2Challenge
import com.processout.sdk.api.model.threeds.PO3DS2Configuration
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.core.ProcessOutResult

/**
 * Delegate interface for [POCheckout3DSService].
 */
interface POCheckout3DSServiceDelegate {

    /**
     * Notifies delegate that service is about to fingerprint device.
     */
    fun willCreateAuthenticationRequest(configuration: PO3DS2Configuration) {}

    /**
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
     * Notifies delegate that service did complete device fingerprinting.
     */
    fun didCreateAuthenticationRequest(result: ProcessOutResult<PO3DS2AuthenticationRequest>) {}

    /**
     * Notifies delegate that implementation is about to handle 3DS2 challenge.
     */
    fun willHandle(challenge: PO3DS2Challenge) {}

    /**
     * Notifies delegate that service did end handling 3DS2 challenge with given result.
     */
    fun didHandle3DS2Challenge(result: ProcessOutResult<Boolean>) {}

    /**
     * Asks delegate to handle 3DS redirect.
     */
    fun handle(
        redirect: PO3DSRedirect,
        callback: (ProcessOutResult<String>) -> Unit
    )
}
