package com.processout.sdk.api.service

import com.processout.sdk.api.model.threeds.PO3DS2AuthenticationRequest
import com.processout.sdk.api.model.threeds.PO3DS2Challenge
import com.processout.sdk.api.model.threeds.PO3DS2Configuration
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.core.ProcessOutResult

interface PO3DSService {

    fun authenticationRequest(
        configuration: PO3DS2Configuration,
        callback: (ProcessOutResult<PO3DS2AuthenticationRequest>) -> Unit
    )

    fun handle(
        challenge: PO3DS2Challenge,
        callback: (ProcessOutResult<Boolean>) -> Unit
    )

    fun handle(
        redirect: PO3DSRedirect,
        callback: (ProcessOutResult<String>) -> Unit
    )

    fun cleanup() {}
}
