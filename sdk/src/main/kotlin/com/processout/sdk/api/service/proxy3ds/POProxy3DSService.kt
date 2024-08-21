package com.processout.sdk.api.service.proxy3ds

import com.processout.sdk.api.service.PO3DSService
import com.processout.sdk.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
interface POProxy3DSService : PO3DSService {

    fun close() {}
}
