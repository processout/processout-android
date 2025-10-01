package com.processout.sdk.api.service.proxy3ds

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.threeds.PO3DS2AuthenticationRequest
import com.processout.sdk.api.model.threeds.PO3DS2Challenge
import com.processout.sdk.api.model.threeds.PO3DS2Configuration
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.api.service.proxy3ds.POProxy3DSServiceRequest.*
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.UUID

/** @suppress */
@ProcessOutInternalApi
class PODefaultProxy3DSService(
    private val scope: CoroutineScope = MainScope()
) : POProxy3DSService {

    private val uuid = UUID.randomUUID()
    private val eventDispatcher = POEventDispatcher.instance

    private var authenticationCallback: ((ProcessOutResult<PO3DS2AuthenticationRequest>) -> Unit)? = null
    private var challengeCallback: ((ProcessOutResult<Boolean>) -> Unit)? = null
    private var redirectCallback: ((ProcessOutResult<String>) -> Unit)? = null

    init {
        eventDispatcher.subscribeForResponse<POProxy3DSServiceResponse>(
            coroutineScope = scope
        ) { response ->
            if (response.uuid != uuid) {
                return@subscribeForResponse
            }
            when (response) {
                is POProxy3DSServiceResponse.Authentication -> authenticationCallback?.invoke(response.result)
                is POProxy3DSServiceResponse.Challenge -> challengeCallback?.invoke(response.result)
                is POProxy3DSServiceResponse.Redirect -> redirectCallback?.invoke(response.result)
                is POProxy3DSServiceResponse.Close -> close()
            }
        }
    }

    override fun authenticationRequest(
        configuration: PO3DS2Configuration,
        callback: (ProcessOutResult<PO3DS2AuthenticationRequest>) -> Unit
    ) {
        authenticationCallback = callback
        dispatch(Authentication(uuid = uuid, configuration = configuration))
    }

    override fun handle(
        challenge: PO3DS2Challenge,
        callback: (ProcessOutResult<Boolean>) -> Unit
    ) {
        challengeCallback = callback
        dispatch(Challenge(uuid = uuid, challenge = challenge))
    }

    override fun handle(
        redirect: PO3DSRedirect,
        callback: (ProcessOutResult<String>) -> Unit
    ) {
        redirectCallback = callback
        dispatch(Redirect(uuid = uuid, redirect = redirect))
    }

    override fun cleanup() {
        authenticationCallback = null
        challengeCallback = null
        redirectCallback = null
        dispatch(Cleanup(uuid = uuid))
    }

    private fun dispatch(request: POProxy3DSServiceRequest) {
        scope.launch { eventDispatcher.send(request) }
    }

    override fun close() {
        scope.cancel()
    }
}
