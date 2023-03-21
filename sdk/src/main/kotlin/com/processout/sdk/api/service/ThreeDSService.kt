package com.processout.sdk.api.service

import com.processout.sdk.api.model.response.PO3DSCustomerAction

internal interface ThreeDSService {

    fun handle(
        action: PO3DSCustomerAction,
        threeDSHandler: PO3DSHandler,
        callback: (PO3DSResult<String>) -> Unit
    )
}
