package com.processout.sdk.api.service

import com.processout.sdk.api.model.response.POCustomerAction

internal interface ThreeDSService {

    fun handle(
        action: POCustomerAction,
        delegate: PO3DSService,
        callback: (PO3DSResult<String>) -> Unit
    )
}
