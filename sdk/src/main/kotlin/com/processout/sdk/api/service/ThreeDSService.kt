package com.processout.sdk.api.service

import com.processout.sdk.api.model.response.POCustomerAction
import com.processout.sdk.core.ProcessOutResult

internal interface ThreeDSService {

    fun handle(
        action: POCustomerAction,
        delegate: PO3DSService,
        callback: (ProcessOutResult<String>) -> Unit
    )
}
