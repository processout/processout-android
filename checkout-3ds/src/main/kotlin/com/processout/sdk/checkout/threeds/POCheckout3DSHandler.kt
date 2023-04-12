package com.processout.sdk.checkout.threeds

import android.app.Activity
import com.checkout.threeds.Environment
import com.checkout.threeds.domain.model.AuthenticationError
import com.checkout.threeds.domain.model.AuthenticationErrorType.*
import com.checkout.threeds.domain.model.ResultType.*
import com.checkout.threeds.standalone.Standalone3DSService
import com.checkout.threeds.standalone.dochallenge.models.ChallengeParameters
import com.checkout.threeds.standalone.models.AuthenticationRequestParameters
import com.checkout.threeds.standalone.models.ConfigParameters
import com.checkout.threeds.standalone.models.DirectoryServerData
import com.checkout.threeds.standalone.models.StandaloneResult
import com.processout.sdk.api.model.request.PO3DS2AuthenticationRequest
import com.processout.sdk.api.model.response.PO3DS2Challenge
import com.processout.sdk.api.model.response.PO3DS2Configuration
import com.processout.sdk.api.model.response.PO3DSRedirect
import com.processout.sdk.api.service.PO3DSHandler
import com.processout.sdk.api.service.PO3DSResult
import com.processout.sdk.checkout.threeds.POCheckout3DSHandlerState.*
import com.processout.sdk.core.POFailure

class POCheckout3DSHandler private constructor(
    private val activity: Activity,
    private val delegate: POCheckout3DSDelegate
) : PO3DSHandler {

    class Builder(
        private val activity: Activity,
        private val delegate: POCheckout3DSDelegate
    ) {
        fun build(): PO3DSHandler = POCheckout3DSHandler(activity, delegate)
    }

    private var state: POCheckout3DSHandlerState = Idle

    override fun authenticationRequest(
        configuration: PO3DS2Configuration,
        callback: (PO3DSResult<PO3DS2AuthenticationRequest>) -> Unit
    ) {
        if (state !is Idle) {
            callback(
                PO3DSResult.Failure(
                    POFailure.Code.Generic(),
                    "3DS2 service is already running."
                )
            )
            return
        }
        try {
            val serviceConfiguration = delegate.configuration(configuration.toConfigParameters())
            val service = Standalone3DSService(Environment.PRODUCTION).initialize(serviceConfiguration)
            val serviceContext = POCheckout3DS2ServiceContext(
                threeDS2Service = service,
                transaction = service.createTransaction()
            )
            state = Fingerprinting(serviceContext)

            val warnings = serviceContext.threeDS2Service.getWarnings().toSet()
            delegate.shouldContinue(warnings) { shouldContinue ->
                if (shouldContinue.not()) {
                    setIdleState(serviceContext)
                    callback(PO3DSResult.Failure(POFailure.Code.Cancelled))
                    return@shouldContinue
                }
                when (val result = serviceContext.transaction.getAuthenticationRequestParameters()) {
                    is StandaloneResult.Success -> {
                        state = Fingerprinted(serviceContext)
                        callback(PO3DSResult.Success(result.value.toAuthenticationRequest()))
                    }
                    is StandaloneResult.Failure -> {
                        setIdleState(serviceContext)
                        callback(result.error.toFailure())
                    }
                }
            }
        } catch (e: Exception) { // FIXME: catch AuthenticationProcessError when it's visible
            callback(PO3DSResult.Failure(POFailure.Code.Generic(), e.message, e))
        }
    }

    override fun handle(challenge: PO3DS2Challenge, callback: (PO3DSResult<Boolean>) -> Unit) {
        if (state !is Fingerprinted) {
            callback(
                PO3DSResult.Failure(
                    POFailure.Code.Generic(),
                    "Unable to handle 3DS2 challenge: not fingerprinted."
                )
            )
            return
        }
        state.doWhenFingerprinted { serviceContext ->
            state = Challenging(serviceContext)
            serviceContext.transaction.doChallenge(
                activity, challenge.toChallengeParameters()
            ) { result ->
                setIdleState(serviceContext)
                when (result.resultType) {
                    Successful -> callback(PO3DSResult.Success(true))
                    Failed -> callback(PO3DSResult.Success(false))
                    Error -> when (result) {
                        is AuthenticationError -> callback(result.toFailure())
                        else -> callback(PO3DSResult.Failure(POFailure.Code.Generic()))
                    }
                }
            }
        }
    }

    override fun handle(redirect: PO3DSRedirect, callback: (PO3DSResult<String>) -> Unit) {
        delegate.handle(redirect, callback)
    }

    override fun cleanup() {
        when (val currentState = state) {
            is Fingerprinted -> setIdleState(currentState.serviceContext)
            else -> {} // Ignore as service is idle or processing and will be cleaned after that.
        }
    }

    private fun setIdleState(serviceContext: POCheckout3DS2ServiceContext) {
        with(serviceContext) {
            transaction.close()
            threeDS2Service.cleanup()
        }
        state = Idle
    }
}

private fun PO3DS2Configuration.toConfigParameters() =
    ConfigParameters(
        directoryServerData = DirectoryServerData(
            directoryServerID = directoryServerId,
            directoryServerPublicKey = directoryServerPublicKey,
            directoryServerRootCertificates = directoryServerRootCAs
        ),
        messageVersion = messageVersion,
        // FIXME: map to supported scheme types
        scheme = scheme ?: String()
    )

private fun AuthenticationRequestParameters.toAuthenticationRequest() =
    PO3DS2AuthenticationRequest(
        deviceData = deviceData,
        sdkAppId = sdkAppId,
        sdkEphemeralPublicKey = sdkEphemeralPublicKey,
        sdkReferenceNumber = sdkReferenceNumber,
        sdkTransactionId = sdkTransactionId
    )

private fun PO3DS2Challenge.toChallengeParameters() =
    ChallengeParameters(
        threeDSServerTransactionID = threeDSServerTransactionId,
        acsTransactionID = acsTransactionId,
        acsRefNumber = acsReferenceNumber,
        acsSignedContent = acsSignedContent
    )

private fun AuthenticationError.toFailure(): PO3DSResult.Failure {
    val code = when (errorType) {
        ConnectivityError -> POFailure.Code.NetworkUnreachable
        AuthenticationProcessError,
        ThreeDS1ProtocolError,
        ThreeDS2ProtocolError,
        InternalError -> POFailure.Code.Generic()
    }
    return PO3DSResult.Failure(code, message = errorCode)
}
