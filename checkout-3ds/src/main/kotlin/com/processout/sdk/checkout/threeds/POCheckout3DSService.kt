@file:Suppress("unused")

package com.processout.sdk.checkout.threeds

import android.app.Activity
import com.checkout.threeds.Environment
import com.checkout.threeds.domain.model.AuthenticationCompleted
import com.checkout.threeds.domain.model.AuthenticationError
import com.checkout.threeds.domain.model.AuthenticationErrorType.*
import com.checkout.threeds.domain.model.ResultType.*
import com.checkout.threeds.standalone.Standalone3DSService
import com.checkout.threeds.standalone.api.ThreeDS2Service
import com.checkout.threeds.standalone.dochallenge.models.ChallengeParameters
import com.checkout.threeds.standalone.models.AuthenticationRequestParameters
import com.checkout.threeds.standalone.models.ConfigParameters
import com.checkout.threeds.standalone.models.DirectoryServerData
import com.checkout.threeds.standalone.models.StandaloneResult
import com.processout.sdk.api.model.threeds.PO3DS2AuthenticationRequest
import com.processout.sdk.api.model.threeds.PO3DS2Challenge
import com.processout.sdk.api.model.threeds.PO3DS2Configuration
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.api.service.PO3DSService
import com.processout.sdk.checkout.threeds.Checkout3DSServiceState.*
import com.processout.sdk.checkout.threeds.CheckoutConstants.*
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.copy

class POCheckout3DSService private constructor(
    private val activity: Activity,
    private val delegate: POCheckout3DSServiceDelegate,
    private val environment: Environment
) : PO3DSService {

    class Builder(
        private val activity: Activity,
        private val delegate: POCheckout3DSServiceDelegate
    ) {
        private var environment: Environment = Environment.PRODUCTION

        fun with(environment: Environment) = apply { this.environment = environment }

        fun build(): PO3DSService = POCheckout3DSService(activity, delegate, environment)
    }

    private var state: Checkout3DSServiceState = Idle

    override fun authenticationRequest(
        configuration: PO3DS2Configuration,
        callback: (ProcessOutResult<PO3DS2AuthenticationRequest>) -> Unit
    ) {
        delegate.willCreateAuthenticationRequest(configuration)
        if (state !is Idle) {
            completeAuthenticationRequest(
                ProcessOutResult.Failure(
                    POFailure.Code.Generic(),
                    "3DS2 service is already running."
                ), callback
            )
            return
        }
        val serviceConfiguration = delegate.configuration(configuration.toConfigParameters())
        Standalone3DSService(environment).let {
            when (val result = it.initialize(serviceConfiguration)) {
                is StandaloneResult.Success -> fingerprint(result.value, callback)
                is StandaloneResult.Failure -> completeAuthenticationRequest(
                    result.error.toFailure(),
                    callback
                )
            }
        }
    }

    private fun fingerprint(
        threeDS2Service: ThreeDS2Service,
        callback: (ProcessOutResult<PO3DS2AuthenticationRequest>) -> Unit
    ) {
        val serviceContext = Checkout3DSServiceContext(
            threeDS2Service = threeDS2Service,
            transaction = threeDS2Service.createTransaction()
        )
        state = Fingerprinting(serviceContext)

        val warnings = serviceContext.threeDS2Service.getWarnings().toSet()
        delegate.shouldContinue(warnings) { shouldContinue ->
            if (shouldContinue.not()) {
                setIdleState(serviceContext)
                completeAuthenticationRequest(
                    ProcessOutResult.Failure(POFailure.Code.Cancelled),
                    callback
                )
                return@shouldContinue
            }
            when (val result = serviceContext.transaction.getAuthenticationRequestParameters()) {
                is StandaloneResult.Success -> {
                    state = Fingerprinted(serviceContext)
                    completeAuthenticationRequest(
                        ProcessOutResult.Success(result.value.toAuthenticationRequest()),
                        callback
                    )
                }
                is StandaloneResult.Failure -> {
                    setIdleState(serviceContext)
                    completeAuthenticationRequest(result.error.toFailure(), callback)
                }
            }
        }
    }

    private fun completeAuthenticationRequest(
        result: ProcessOutResult<PO3DS2AuthenticationRequest>,
        callback: (ProcessOutResult<PO3DS2AuthenticationRequest>) -> Unit
    ) {
        delegate.didCreateAuthenticationRequest(result.copy())
        callback(result.copy())
    }

    override fun handle(challenge: PO3DS2Challenge, callback: (ProcessOutResult<Boolean>) -> Unit) {
        delegate.willHandle(challenge)
        if (state !is Fingerprinted) {
            failChallenge(
                ProcessOutResult.Failure(
                    POFailure.Code.Generic(),
                    "Unable to handle 3DS2 challenge: not fingerprinted."
                ), callback
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
                    Completed -> when (result) {
                        is AuthenticationCompleted -> completeChallenge(result.transactionStatus, callback)
                        else -> failChallenge(ProcessOutResult.Failure(POFailure.Code.Generic()), callback)
                    }
                    Error -> when (result) {
                        is AuthenticationError -> failChallenge(result.toFailure(), callback)
                        else -> failChallenge(ProcessOutResult.Failure(POFailure.Code.Generic()), callback)
                    }
                }
            }
        }
    }

    private fun completeChallenge(
        transactionStatus: String,
        callback: (ProcessOutResult<Boolean>) -> Unit
    ) {
        val success = ProcessOutResult.Success(transactionStatus.uppercase() == "Y")
        delegate.didHandle3DS2Challenge(success)
        callback(success.copy())
    }

    private fun failChallenge(
        failure: ProcessOutResult.Failure,
        callback: (ProcessOutResult<Boolean>) -> Unit
    ) {
        delegate.didHandle3DS2Challenge(failure.copy())
        callback(failure.copy())
    }

    override fun handle(redirect: PO3DSRedirect, callback: (ProcessOutResult<String>) -> Unit) {
        delegate.handle(redirect, callback)
    }

    override fun cleanup() {
        when (val currentState = state) {
            is Fingerprinted -> setIdleState(currentState.serviceContext)
            else -> {} // Ignore as service is idle or processing and will be cleaned after that.
        }
    }

    private fun setIdleState(serviceContext: Checkout3DSServiceContext) {
        with(serviceContext) {
            transaction.close()
            threeDS2Service.cleanup()
        }
        state = Idle
    }
}

/**
 * Card schemes supported by Checkout.
 */
private enum class CheckoutCardScheme(val rawType: String) {
    VISA("visa"),
    MASTERCARD("mastercard"),
    UNKNOWN(String())
}

private fun PO3DS2Configuration.checkoutSchemeType() =
    CheckoutCardScheme::rawType.findBy(scheme) ?: CheckoutCardScheme.UNKNOWN

private fun PO3DS2Configuration.toConfigParameters() =
    ConfigParameters(
        directoryServerData = DirectoryServerData(
            directoryServerID = directoryServerId,
            directoryServerPublicKey = directoryServerPublicKey,
            directoryServerRootCertificates = directoryServerRootCertificates
        ),
        messageVersion = messageVersion,
        scheme = checkoutSchemeType().rawType
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

private fun AuthenticationError.toFailure(): ProcessOutResult.Failure {
    val code = when (errorType) {
        AuthenticationProcessError -> when (errorCode) {
            AuthenticationProcessErrorCodes.E1002_Challenge_cancelled -> POFailure.Code.Cancelled
            AuthenticationProcessErrorCodes.E1003_Challenge_timeout -> POFailure.Code.Timeout()
            else -> POFailure.Code.Generic()
        }
        ConnectivityError -> when (errorCode) {
            ConnectivityErrorCode.E2001_connection_failed -> POFailure.Code.NetworkUnreachable
            ConnectivityErrorCode.E2002_connection_timeout -> POFailure.Code.Timeout()
            else -> POFailure.Code.Generic()
        }
        ThreeDS1ProtocolError,
        ThreeDS2ProtocolError,
        InternalError -> POFailure.Code.Generic()
    }
    return ProcessOutResult.Failure(code, message = errorCode)
}
