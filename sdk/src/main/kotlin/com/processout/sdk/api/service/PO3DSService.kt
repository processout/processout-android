package com.processout.sdk.api.service

import com.processout.sdk.api.model.threeds.PO3DS2AuthenticationRequest
import com.processout.sdk.api.model.threeds.PO3DS2Challenge
import com.processout.sdk.api.model.threeds.PO3DS2Configuration
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.core.ProcessOutResult

/**
 * Interface for service implementation that handles 3-D Secure transactions.
 */
interface PO3DSService {

    /**
     * Creates [PO3DS2AuthenticationRequest] that will be passed to 3DS Server for the AReq.
     */
    fun authenticationRequest(
        configuration: PO3DS2Configuration,
        callback: (ProcessOutResult<PO3DS2AuthenticationRequest>) -> Unit
    )

    /**
     * Handles the given 3DS2 challenge and callbacks with the boolean result:
     * _true_ if challenge was handled successfully and
     * _false_ if transaction was denied.
     * In all other cases callback with the [ProcessOutResult.Failure] indicating what went wrong.
     */
    fun handle(
        challenge: PO3DS2Challenge,
        callback: (ProcessOutResult<Boolean>) -> Unit
    )

    /**
     * Handles 3DS redirect.
     * If [PO3DSRedirect.timeoutSeconds] is available it must be respected
     * and should callback when timeout is reached with:
     * ```
     * ProcessOutResult.Failure(POFailure.Code.Timeout(POFailure.TimeoutCode.mobile))
     * ```
     */
    fun handle(
        redirect: PO3DSRedirect,
        callback: (ProcessOutResult<String>) -> Unit
    )

    /**
     * Cleanup allocated resources.
     */
    fun cleanup() {}
}
