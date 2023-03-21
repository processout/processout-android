package com.processout.sdk.api.service

import com.processout.sdk.api.model.response.POCustomerAction

internal interface ThreeDSService {

    fun handle(
        action: POCustomerAction,
        threeDSHandler: PO3DSHandler,
        callback: (PO3DSResult<String>) -> Unit
    )
}
