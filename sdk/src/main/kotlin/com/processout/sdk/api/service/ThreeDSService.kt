package com.processout.sdk.api.service

import com.processout.sdk.api.model.response.CustomerAction
import com.processout.sdk.core.ProcessOutResult

internal interface ThreeDSService {

    fun handle(
        action: CustomerAction,
        delegate: PO3DSService,
        callback: (ProcessOutResult<String>) -> Unit
    )
}
