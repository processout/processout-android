package com.processout.sdk.checkout.threeds

import android.app.Activity
import com.checkout.threeds.Environment
import com.checkout.threeds.domain.model.AuthenticationError
import com.checkout.threeds.domain.model.AuthenticationErrorType.*
import com.checkout.threeds.domain.model.ResultType.*
import com.checkout.threeds.standalone.Standalone3DSService
import com.checkout.threeds.standalone.api.ThreeDS2Service
import com.checkout.threeds.standalone.api.Transaction
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

    private lateinit var serviceContext: ServiceContext

    override fun authenticationRequest(
        configuration: PO3DS2Configuration,
        callback: (PO3DSResult<PO3DS2AuthenticationRequest>) -> Unit
    ) {
        try {
            val serviceConfiguration = delegate.configuration(configuration.toConfigParameters())
            Standalone3DSService(Environment.PRODUCTION)
                .initialize(serviceConfiguration)
                .also { service ->
                    serviceContext = ServiceContext(
                        threeDS2Service = service,
                        transaction = service.createTransaction()
                    )
                }

            val warnings = serviceContext.threeDS2Service.getWarnings().toSet()
            delegate.shouldContinue(warnings) { shouldContinue ->
                if (shouldContinue.not()) {
                    cleanup()
                    callback(PO3DSResult.Failure(POFailure.Code.Cancelled))
                    return@shouldContinue
                }
                when (val result = serviceContext.transaction.getAuthenticationRequestParameters()) {
                    is StandaloneResult.Success -> callback(
                        PO3DSResult.Success(result.value.toAuthenticationRequest())
                    )
                    is StandaloneResult.Failure -> {
                        cleanup()
                        callback(result.error.toFailure())
                    }
                }
            }
        } catch (e: Exception) {
            if (::serviceContext.isInitialized) cleanup()
            callback(PO3DSResult.Failure(POFailure.Code.Generic(), e.message, e))
        }
    }

    override fun handle(challenge: PO3DS2Challenge, callback: (PO3DSResult<Boolean>) -> Unit) {
        try {
            serviceContext.transaction.doChallenge(
                activity, challenge.toChallengeParameters()
            ) { result ->
                cleanup()
                when (result.resultType) {
                    Successful -> callback(PO3DSResult.Success(true))
                    Failed -> callback(PO3DSResult.Success(false))
                    Error -> if (result is AuthenticationError)
                        callback(result.toFailure())
                    else callback(PO3DSResult.Failure(POFailure.Code.Generic()))
                }
            }
        } catch (e: Exception) {
            cleanup()
            callback(PO3DSResult.Failure(POFailure.Code.Generic(), e.message, e))
        }
    }

    override fun handle(redirect: PO3DSRedirect, callback: (PO3DSResult<String>) -> Unit) {
        delegate.handle(redirect, callback)
    }

    private fun cleanup() {
        if (::serviceContext.isInitialized)
            with(serviceContext) {
                transaction.close()
                threeDS2Service.cleanup()
            }
    }

    private data class ServiceContext(
        val threeDS2Service: ThreeDS2Service,
        val transaction: Transaction
    )
}

private fun PO3DS2Configuration.toConfigParameters() =
    ConfigParameters(
        directoryServerData = DirectoryServerData(
            directoryServerID = directoryServerId,
            directoryServerPublicKey = directoryServerPublicKey,
            // FIXME: pass collection of certificates when supported
            directoryServerRootCertificate = directoryServerRootCAs[1]
        ),
        messageVersion = messageVersion,
        // FIXME: map to supported scheme types
        scheme = scheme ?: String()
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
