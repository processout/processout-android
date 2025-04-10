package com.processout.sdk.api.service

import com.processout.sdk.api.model.response.CustomerAction
import com.processout.sdk.core.ProcessOutResult

internal interface CustomerActionsService {

    suspend fun handle(
        action: CustomerAction,
        threeDSService: PO3DSService
    ): ProcessOutResult<String>
}
