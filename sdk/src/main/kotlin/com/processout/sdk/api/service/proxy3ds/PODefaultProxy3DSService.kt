package com.processout.sdk.api.service.proxy3ds

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.threeds.PO3DS2AuthenticationRequest
import com.processout.sdk.api.model.threeds.PO3DS2Challenge
import com.processout.sdk.api.model.threeds.PO3DS2Configuration
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.api.service.proxy3ds.POProxy3DSServiceRequest.*
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.coroutines.*

/** @suppress */
@ProcessOutInternalApi
class PODefaultProxy3DSService(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
    private val eventDispatcher: POEventDispatcher = POEventDispatcher
) : POProxy3DSService {

    private var authenticationCallback: ((ProcessOutResult<PO3DS2AuthenticationRequest>) -> Unit)? = null
    private var challengeCallback: ((ProcessOutResult<Boolean>) -> Unit)? = null
    private var redirectCallback: ((ProcessOutResult<String>) -> Unit)? = null

    init {
        eventDispatcher.subscribeForResponse<POProxy3DSServiceResponse>(
            coroutineScope = scope
        ) { response ->
            when (response) {
                is POProxy3DSServiceResponse.Authentication -> authenticationCallback?.invoke(response.result)
                is POProxy3DSServiceResponse.Challenge -> challengeCallback?.invoke(response.result)
                is POProxy3DSServiceResponse.Redirect -> redirectCallback?.invoke(response.result)
            }
        }
    }

    override fun authenticationRequest(
        configuration: PO3DS2Configuration,
        callback: (ProcessOutResult<PO3DS2AuthenticationRequest>) -> Unit
    ) {
        authenticationCallback = callback
        scope.launch {
            eventDispatcher.send(Authentication(configuration = configuration))
        }
    }

    override fun handle(
        challenge: PO3DS2Challenge,
        callback: (ProcessOutResult<Boolean>) -> Unit
    ) {
        challengeCallback = callback
        scope.launch {
            eventDispatcher.send(Challenge(challenge = challenge))
        }
    }

    override fun handle(
        redirect: PO3DSRedirect,
        callback: (ProcessOutResult<String>) -> Unit
    ) {
        redirectCallback = callback
        scope.launch {
            eventDispatcher.send(Redirect(redirect = redirect))
        }
    }

    override fun cleanup() {
        authenticationCallback = null
        challengeCallback = null
        redirectCallback = null
        scope.launch {
            eventDispatcher.send(Cleanup())
        }
    }

    override fun close() {
        scope.cancel()
    }
}
