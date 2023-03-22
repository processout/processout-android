package com.processout.sdk.api.service

import com.processout.sdk.api.model.request.PO3DS2AuthenticationRequest
import com.processout.sdk.api.model.response.PO3DS2Challenge
import com.processout.sdk.api.model.response.PO3DS2Configuration
import com.processout.sdk.api.model.response.PO3DSRedirect

interface PO3DSService {

    fun authenticationRequest(
        configuration: PO3DS2Configuration,
        callback: (PO3DSResult<PO3DS2AuthenticationRequest>) -> Unit
    )

    fun handle(
        challenge: PO3DS2Challenge,
        callback: (PO3DSResult<Boolean>) -> Unit
    )

    fun handle(
        redirect: PO3DSRedirect,
        callback: (PO3DSResult<String>) -> Unit
    )
}
