package com.processout.sdk.netcetera.threeds

import com.processout.sdk.api.model.threeds.PO3DS2AuthenticationRequest
import com.processout.sdk.api.model.threeds.PO3DS2Challenge
import com.processout.sdk.api.model.threeds.PO3DS2Configuration
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.api.service.PO3DSService
import com.processout.sdk.core.ProcessOutResult

class PONetcetera3DS2Service(
    private val delegate: PONetcetera3DS2ServiceDelegate
) : PO3DSService {

    override fun authenticationRequest(
        configuration: PO3DS2Configuration,
        callback: (ProcessOutResult<PO3DS2AuthenticationRequest>) -> Unit
    ) {
        // TODO
    }

    override fun handle(
        challenge: PO3DS2Challenge,
        callback: (ProcessOutResult<Boolean>) -> Unit
    ) {
        // TODO
    }

    override fun handle(
        redirect: PO3DSRedirect,
        callback: (ProcessOutResult<String>) -> Unit
    ) {
        // TODO
    }

    override fun cleanup() {
        // TODO
    }
}
