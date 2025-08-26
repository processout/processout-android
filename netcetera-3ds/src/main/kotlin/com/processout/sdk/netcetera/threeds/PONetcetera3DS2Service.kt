@file:Suppress("unused")

package com.processout.sdk.netcetera.threeds

import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.netcetera.threeds.sdk.ThreeDS2ServiceInstance
import com.netcetera.threeds.sdk.api.configparameters.ConfigParameters
import com.netcetera.threeds.sdk.api.configparameters.builder.ConfigurationBuilder
import com.netcetera.threeds.sdk.api.configparameters.builder.SchemeConfiguration
import com.netcetera.threeds.sdk.api.security.Severity
import com.netcetera.threeds.sdk.api.transaction.AuthenticationRequestParameters
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeStatusReceiver
import com.netcetera.threeds.sdk.api.transaction.challenge.events.CompletionEvent
import com.netcetera.threeds.sdk.api.transaction.challenge.events.ProtocolErrorEvent
import com.netcetera.threeds.sdk.api.transaction.challenge.events.RuntimeErrorEvent
import com.netcetera.threeds.sdk.api.utils.DsRidValues
import com.processout.sdk.api.model.threeds.PO3DS2AuthenticationRequest
import com.processout.sdk.api.model.threeds.PO3DS2Challenge
import com.processout.sdk.api.model.threeds.PO3DS2Configuration
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.api.service.PO3DSService
import com.processout.sdk.core.POFailure.Code.*
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import com.processout.sdk.netcetera.threeds.Netcetera3DS2ServiceState.*
import com.processout.sdk.netcetera.threeds.PONetcetera3DS2ServiceConfiguration.AuthenticationMode.COMPATIBILITY
import com.processout.sdk.netcetera.threeds.core.BuildConfig
import com.processout.sdk.netcetera.threeds.core.poApiKey
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.io.Encoders
import io.jsonwebtoken.security.Jwks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicReference

/**
 * A service that integrates the Netcetera 3DS SDK.
 *
 * @param[delegate] Delegate that handles communication with the service.
 * @param[configuration] Service configuration.
 */
class PONetcetera3DS2Service(
    private val delegate: PONetcetera3DS2ServiceDelegate,
    private val configuration: PONetcetera3DS2ServiceConfiguration = PONetcetera3DS2ServiceConfiguration()
) : PO3DSService {

    companion object {
        /** Netcetera 3DS SDK version. */
        const val VERSION = BuildConfig.LIBRARY_VERSION
    }

    private data class Encryption(
        val publicKey: String,
        val publicKeyId: String?
    )

    private data class Warning(
        val id: String,
        val message: String,
        val severity: Severity
    )

    private val state = AtomicReference<Netcetera3DS2ServiceState>(Idle)

    //region Authentication Request

    override fun authenticationRequest(
        configuration: PO3DS2Configuration,
        callback: (ProcessOutResult<PO3DS2AuthenticationRequest>) -> Unit
    ) {
        if (!state.compareAndSet(Idle, Starting)) {
            return completeAuthenticationRequest(
                result = ProcessOutResult.Failure(
                    code = Generic(),
                    message = "Service is already running."
                ),
                callback = callback
            )
        }
        POLogger.info("Service is started: creating authentication request.")
        val activity = delegate.activity() ?: return completeAuthenticationRequest(
            result = ProcessOutResult.Failure(
                code = Generic(),
                message = "Activity instance is null."
            ),
            callback = callback
        )
        val applicationContext = activity.applicationContext
        activity.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                initialize(applicationContext, configuration, callback)
            }
        }
    }

    private suspend fun initialize(
        applicationContext: Context,
        configuration: PO3DS2Configuration,
        callback: (ProcessOutResult<PO3DS2AuthenticationRequest>) -> Unit
    ) {
        configParameters(configuration)
            .onSuccess { configParameters ->
                try {
                    val threeDS2Service = ThreeDS2ServiceInstance.get()
                    threeDS2Service.initialize(
                        applicationContext,
                        configParameters,
                        this@PONetcetera3DS2Service.configuration.locale?.toLanguageTag(),
                        this@PONetcetera3DS2Service.configuration.uiCustomizations
                    )
                    val serviceContext = Netcetera3DS2ServiceContext(
                        applicationContext = applicationContext,
                        threeDS2Service = threeDS2Service
                    )
                    state.set(Started(serviceContext))
                    fingerprint(serviceContext, configuration, callback)
                } catch (e: Exception) {
                    completeAuthenticationRequest(
                        result = ProcessOutResult.Failure(
                            code = Generic(),
                            message = "Initialization failure.",
                            cause = e
                        ),
                        callback = callback
                    )
                }
            }.onFailure { failure ->
                completeAuthenticationRequest(
                    result = failure,
                    callback = callback
                )
            }
    }

    private fun configParameters(
        configuration: PO3DS2Configuration
    ): ProcessOutResult<ConfigParameters> {
        return try {
            val configParameters = ConfigurationBuilder()
                .poApiKey()
                .apply {
                    customScheme(configuration)?.let { schemeConfiguration ->
                        configureScheme(schemeConfiguration)
                    }
                }.apply {
                    if (this@PONetcetera3DS2Service.configuration.authenticationMode == COMPATIBILITY) {
                        // Restrict parameters that produce large values to ensure compatibility
                        // with payment providers that impose size limit on authentication request payload
                        // (e.g., Stripe limits payload to 5000 characters).
                        //
                        // A071: Default input method.
                        // A074: Enabled input methods.
                        // A125: An array of non-system application packages that are installed on the device.
                        restrictedParameters(listOf("A071", "A074", "A125"))
                    }
                }.build()
            ProcessOutResult.Success(value = configParameters)
        } catch (e: Exception) {
            ProcessOutResult.Failure(
                code = Generic(),
                message = "Failed to create configuration parameters.",
                cause = e
            )
        }
    }

    private fun customScheme(
        configuration: PO3DS2Configuration
    ): SchemeConfiguration? {
        val supportedIds = setOf(
            DsRidValues.MASTERCARD,
            DsRidValues.VISA,
            DsRidValues.AMEX,
            DsRidValues.DINERS,
            DsRidValues.UNION,
            DsRidValues.JCB,
            DsRidValues.CB,
            DsRidValues.EFTPOS
        )
        if (supportedIds.contains(configuration.directoryServerId)) {
            return null
        }
        val encryption = extractEncryption(jwkBase64 = configuration.directoryServerPublicKey)
        return SchemeConfiguration
            .newSchemeConfiguration(configuration.scheme ?: "<unknown>")
            .ids(listOf(configuration.directoryServerId))
            .logo(R.drawable.po_logo_placeholder.toString())
            .encryptionPublicKey(
                encryption.publicKey,
                encryption.publicKeyId
            ).apply {
                val rootCertificates = configuration.directoryServerRootCertificates.map { ensurePadded(base64 = it) }
                if (rootCertificates.isNotEmpty()) {
                    rootPublicKey(*rootCertificates.toTypedArray())
                }
            }.build()
    }

    @Suppress("UNCHECKED_CAST")
    private fun extractEncryption(jwkBase64: String): Encryption {
        val jwkJson = String(
            bytes = Decoders.BASE64URL.decode(jwkBase64),
            charset = StandardCharsets.UTF_8
        )
        val jwk = Jwks.parser().build().parse(jwkJson)
        val x5c = jwk["x5c"] as? List<String>
        val publicKey = x5c?.firstOrNull() ?: run {
            Encoders.BASE64.encode(jwk.toKey().encoded)
        }
        return Encryption(
            publicKey = publicKey,
            publicKeyId = jwk["kid"] as? String
        )
    }

    private fun ensurePadded(base64: String): String {
        val paddingLength = (4 - base64.length % 4) % 4
        return base64 + "=".repeat(paddingLength)
    }

    private suspend fun fingerprint(
        serviceContext: Netcetera3DS2ServiceContext,
        configuration: PO3DS2Configuration,
        callback: (ProcessOutResult<PO3DS2AuthenticationRequest>) -> Unit
    ) {
        try {
            val transaction = serviceContext.threeDS2Service.createTransaction(
                configuration.directoryServerId,
                configuration.messageVersion
            )
            this@PONetcetera3DS2Service.configuration.bridgingExtensionVersion?.let {
                transaction.useBridgingExtension(it)
            }
            val serviceContext = serviceContext.copy(
                transaction = transaction,
                transactionId = configuration.directoryServerTransactionId
            )
            state.set(Fingerprinting(serviceContext))

            val warnings = serviceContext.threeDS2Service.warnings.toSet()
            val shouldContinue = delegate.shouldContinue(warnings)
            if (!shouldContinue) {
                return completeAuthenticationRequest(
                    result = ProcessOutResult.Failure(
                        code = Cancelled,
                        message = "Cancelled due to the warnings: ${warnings.map { it.map() }}"
                    ),
                    callback = callback
                )
            }

            val authenticationRequest = transaction.authenticationRequestParameters.toAuthenticationRequest()
            state.set(Fingerprinted(serviceContext))
            completeAuthenticationRequest(
                result = ProcessOutResult.Success(value = authenticationRequest),
                callback = callback
            )
        } catch (e: Exception) {
            completeAuthenticationRequest(
                result = ProcessOutResult.Failure(
                    code = Generic(),
                    message = "Transaction failure.",
                    cause = e
                ),
                callback = callback
            )
        }
    }

    private fun com.netcetera.threeds.sdk.api.security.Warning.map() =
        Warning(
            id = id,
            message = message,
            severity = severity
        )

    private fun AuthenticationRequestParameters.toAuthenticationRequest() =
        PO3DS2AuthenticationRequest(
            deviceData = deviceData,
            sdkAppId = sdkAppID,
            sdkEphemeralPublicKey = sdkEphemeralPublicKey,
            sdkReferenceNumber = sdkReferenceNumber,
            sdkTransactionId = sdkTransactionID
        )

    private fun completeAuthenticationRequest(
        result: ProcessOutResult<PO3DS2AuthenticationRequest>,
        callback: (ProcessOutResult<PO3DS2AuthenticationRequest>) -> Unit
    ) {
        result.onFailure { failure ->
            POLogger.info("Failed to create authentication request: %s", failure)
        }
        callback(result)
    }

    //endregion

    //region 3DS2 Challenge

    override fun handle(
        challenge: PO3DS2Challenge,
        callback: (ProcessOutResult<Boolean>) -> Unit
    ) {
        POLogger.info("Handling 3DS2 challenge.")
        val currentState = state.get()
        if (currentState !is Fingerprinted) {
            return completeChallenge(
                result = ProcessOutResult.Failure(
                    code = Generic(),
                    message = "Invalid state: not fingerprinted."
                ),
                callback = callback
            )
        }
        currentState.whenFingerprinted { serviceContext ->
            val activity = delegate.activity() ?: return@whenFingerprinted completeChallenge(
                result = ProcessOutResult.Failure(
                    code = Generic(),
                    message = "Activity instance is null."
                ),
                callback = callback
            )
            val transaction = serviceContext.transaction
            if (transaction == null || serviceContext.transactionId != challenge.threeDSServerTransactionId) {
                return@whenFingerprinted completeChallenge(
                    result = ProcessOutResult.Failure(
                        code = Generic(),
                        message = "Failed to resolve transaction."
                    ),
                    callback = callback
                )
            }
            state.set(Challenging(serviceContext))
            try {
                transaction.doChallenge(
                    activity,
                    challenge.toChallengeParameters(
                        sdkTransactionId = transaction.authenticationRequestParameters.sdkTransactionID
                    ),
                    DefaultChallengeStatusReceiver(callback),
                    configuration.challengeTimeoutSeconds / 60
                )
            } catch (e: Exception) {
                completeChallenge(
                    result = ProcessOutResult.Failure(
                        code = Generic(),
                        message = "Challenge initialization failure.",
                        cause = e
                    ),
                    callback = callback
                )
            }
        }
    }

    @Suppress("UsePropertyAccessSyntax")
    private fun PO3DS2Challenge.toChallengeParameters(
        sdkTransactionId: String
    ) = let {
        ChallengeParameters().apply {
            set3DSServerTransactionID(it.threeDSServerTransactionId)
            setAcsTransactionID(it.acsTransactionId)
            setAcsRefNumber(it.acsReferenceNumber)
            setAcsSignedContent(it.acsSignedContent)
            configuration.returnUrl?.let { returnUrl ->
                val uri = returnUrl.toUri().buildUpon()
                    .appendQueryParameter("transID", sdkTransactionId)
                    .build()
                setThreeDSRequestorAppURL(uri.toString())
            }
        }
    }

    private inner class DefaultChallengeStatusReceiver(
        private val callback: (ProcessOutResult<Boolean>) -> Unit
    ) : ChallengeStatusReceiver {

        override fun completed(event: CompletionEvent) {
            val isVerified = event.transactionStatus == "Y"
            if (!isVerified) {
                POLogger.info("Failed 3DS2 challenge verification: %s", event.toString())
            }
            completeChallenge(
                result = ProcessOutResult.Success(value = isVerified),
                callback = callback
            )
        }

        override fun cancelled() {
            completeChallenge(
                result = ProcessOutResult.Failure(
                    code = Cancelled,
                    message = "Challenge was cancelled by the user."
                ),
                callback = callback
            )
        }

        override fun timedout() {
            completeChallenge(
                result = ProcessOutResult.Failure(
                    code = Timeout(),
                    message = "Challenge timed out."
                ),
                callback = callback
            )
        }

        override fun protocolError(event: ProtocolErrorEvent) {
            completeChallenge(
                result = ProcessOutResult.Failure(
                    code = Generic(),
                    message = event.toString()
                ),
                callback = callback
            )
        }

        override fun runtimeError(event: RuntimeErrorEvent) {
            completeChallenge(
                result = ProcessOutResult.Failure(
                    code = Generic(),
                    message = event.toString()
                ),
                callback = callback
            )
        }
    }

    private fun completeChallenge(
        result: ProcessOutResult<Boolean>,
        callback: (ProcessOutResult<Boolean>) -> Unit
    ) {
        result.onFailure { failure ->
            POLogger.info("Failed to handle 3DS2 challenge: %s", failure)
        }
        callback(result)
    }

    //endregion

    override fun handle(
        redirect: PO3DSRedirect,
        callback: (ProcessOutResult<String>) -> Unit
    ) {
        POLogger.info("Delegating 3DS redirect.")
        delegate.handle(redirect, callback)
    }

    override fun cleanup() {
        val serviceContext = when (val state = state.get()) {
            is Started -> state.serviceContext
            is Fingerprinting -> state.serviceContext
            is Fingerprinted -> state.serviceContext
            is Challenging -> state.serviceContext
            else -> null
        }
        serviceContext?.let {
            try {
                it.transaction?.close()
                it.threeDS2Service.cleanup(it.applicationContext)
            } catch (e: Exception) {
                // ignore
            }
        }
        state.set(Idle)
        POLogger.info("Service is cleaned and idle.")
    }
}
