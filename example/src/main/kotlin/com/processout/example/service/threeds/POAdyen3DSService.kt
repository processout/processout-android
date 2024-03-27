package com.processout.example.service.threeds

import android.app.Activity
import com.adyen.threeds2.*
import com.adyen.threeds2.parameters.ChallengeParameters
import com.adyen.threeds2.util.AdyenConfigParameters
import com.processout.sdk.api.model.threeds.PO3DS2AuthenticationRequest
import com.processout.sdk.api.model.threeds.PO3DS2Challenge
import com.processout.sdk.api.model.threeds.PO3DS2Configuration
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.api.service.PO3DSService
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.threeds.PO3DSRedirectCustomTabLauncher

class POAdyen3DSService(
    private val activity: Activity,
    private val customTabLauncher: PO3DSRedirectCustomTabLauncher,
    private val returnUrl: String
) : PO3DSService {

    private var transaction: Transaction? = null

    override fun authenticationRequest(
        configuration: PO3DS2Configuration,
        callback: (ProcessOutResult<PO3DS2AuthenticationRequest>) -> Unit
    ) {
        try {
            val adyenConfiguration = AdyenConfigParameters.Builder(
                configuration.directoryServerId,
                configuration.directoryServerPublicKey,
                null // directoryServerRootCertificates
            ).build()

            ThreeDS2Service.INSTANCE.initialize(
                activity,
                adyenConfiguration,
                // Optional properties.
                null, // locale
                null // uiCustomization
            )

            transaction = ThreeDS2Service.INSTANCE.createTransaction(
                null, // directoryServerID
                configuration.messageVersion
            ).also {
                val authenticationRequest = it.authenticationRequestParameters.toAuthenticationRequest()
                callback(ProcessOutResult.Success(authenticationRequest))
            }
        } catch (e: Exception) {
            // Handle specific Adyen exceptions.
            callback(ProcessOutResult.Failure(POFailure.Code.Generic(), cause = e))
        }
    }

    private fun AuthenticationRequestParameters.toAuthenticationRequest() =
        PO3DS2AuthenticationRequest(
            deviceData = deviceData,
            sdkAppId = sdkAppID,
            sdkEphemeralPublicKey = sdkEphemeralPublicKey,
            sdkReferenceNumber = sdkReferenceNumber,
            sdkTransactionId = sdkTransactionID
        )

    override fun handle(
        challenge: PO3DS2Challenge,
        callback: (ProcessOutResult<Boolean>) -> Unit
    ) {
        val challengeParameters = ChallengeParameters().apply {
            set3DSServerTransactionID(challenge.threeDSServerTransactionId)
            acsTransactionID = challenge.acsTransactionId
            acsRefNumber = challenge.acsReferenceNumber
            acsSignedContent = challenge.acsSignedContent
        }

        val challengeStatusHandler = ChallengeStatusHandler { challengeResult ->
            when (challengeResult) {
                is ChallengeResult.Completed -> {
                    val status = challengeResult.transactionStatus.uppercase() == "Y"
                    callback(ProcessOutResult.Success(status))
                }
                is ChallengeResult.Cancelled -> callback(
                    ProcessOutResult.Failure(
                        POFailure.Code.Cancelled,
                        challengeResult.transactionStatus
                    )
                )
                is ChallengeResult.Timeout -> callback(
                    ProcessOutResult.Failure(
                        POFailure.Code.Timeout(),
                        challengeResult.transactionStatus
                    )
                )
                is ChallengeResult.Error -> callback(
                    ProcessOutResult.Failure(
                        POFailure.Code.Generic(),
                        challengeResult.transactionStatus
                    )
                )
            }
        }

        try {
            transaction?.doChallenge(
                activity,
                challengeParameters,
                challengeStatusHandler,
                5 // Timeout in minutes.
            ) ?: run {
                ThreeDS2Service.INSTANCE.cleanup(activity)
                callback(
                    ProcessOutResult.Failure(
                        POFailure.Code.Internal(),
                        "Cannot perform challenge. Transaction is not initialized."
                    )
                )
            }
        } catch (e: Exception) {
            // Handle specific Adyen exceptions.
            callback(ProcessOutResult.Failure(POFailure.Code.Generic(), cause = e))
        }
    }

    override fun handle(redirect: PO3DSRedirect, callback: (ProcessOutResult<String>) -> Unit) {
        customTabLauncher.launch(redirect, returnUrl, callback)
    }

    override fun cleanup() {
        transaction?.let {
            it.close()
            ThreeDS2Service.INSTANCE.cleanup(activity)
            transaction = null
        }
    }
}
