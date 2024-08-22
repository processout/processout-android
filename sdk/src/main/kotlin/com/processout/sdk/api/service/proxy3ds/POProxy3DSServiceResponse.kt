package com.processout.sdk.api.service.proxy3ds

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.threeds.PO3DS2AuthenticationRequest
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import java.util.UUID

/** @suppress */
@ProcessOutInternalApi
sealed interface POProxy3DSServiceResponse : POEventDispatcher.Response {

    data class Authentication(
        override val uuid: UUID,
        val result: ProcessOutResult<PO3DS2AuthenticationRequest>
    ) : POProxy3DSServiceResponse

    data class Challenge(
        override val uuid: UUID,
        val result: ProcessOutResult<Boolean>
    ) : POProxy3DSServiceResponse

    data class Redirect(
        override val uuid: UUID,
        val result: ProcessOutResult<String>
    ) : POProxy3DSServiceResponse
}
