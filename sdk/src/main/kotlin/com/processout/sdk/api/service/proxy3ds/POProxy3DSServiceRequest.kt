package com.processout.sdk.api.service.proxy3ds

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.threeds.PO3DS2Challenge
import com.processout.sdk.api.model.threeds.PO3DS2Configuration
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import java.util.UUID

/** @suppress */
@ProcessOutInternalApi
sealed interface POProxy3DSServiceRequest : POEventDispatcher.Request {

    data class Authentication(
        override val uuid: UUID = UUID.randomUUID(),
        val configuration: PO3DS2Configuration
    ) : POProxy3DSServiceRequest

    data class Challenge(
        override val uuid: UUID = UUID.randomUUID(),
        val challenge: PO3DS2Challenge
    ) : POProxy3DSServiceRequest

    data class Redirect(
        override val uuid: UUID = UUID.randomUUID(),
        val redirect: PO3DSRedirect
    ) : POProxy3DSServiceRequest

    data class Cleanup(
        override val uuid: UUID = UUID.randomUUID()
    ) : POProxy3DSServiceRequest
}
