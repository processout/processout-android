package com.processout.sdk.api.service

import com.processout.sdk.api.model.response.CustomerAction
import com.processout.sdk.core.ProcessOutResult

internal interface CustomerActionsService {

    fun handle(
        action: CustomerAction,
        threeDSService: PO3DSService,
        callback: (ProcessOutResult<String>) -> Unit
    )
}
