package com.processout.sdk.api.service

import android.util.Base64
import com.processout.sdk.api.model.response.CustomerAction
import com.processout.sdk.api.model.response.CustomerAction.Type.*
import com.processout.sdk.api.model.threeds.PO3DS2AuthenticationRequest
import com.processout.sdk.api.model.threeds.PO3DS2Challenge
import com.processout.sdk.api.model.threeds.PO3DS2Configuration
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.core.POFailure.Code.Internal
import com.processout.sdk.core.POFailure.Code.Timeout
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import java.net.MalformedURLException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class DefaultCustomerActionsService(
    private val moshi: Moshi
) : CustomerActionsService {

    private companion object {
        const val DEVICE_CHANNEL = "app"
        const val GATEWAY_TOKEN_PREFIX = "gway_req_"
        const val CHALLENGE_SUCCESS_GATEWAY_REQUEST_BODY = """{ "transStatus": "Y" }"""
        const val CHALLENGE_FAILURE_GATEWAY_REQUEST_BODY = """{ "transStatus": "N" }"""
        const val WEB_FINGERPRINT_TIMEOUT_GATEWAY_REQUEST_BODY = """{ "threeDS2FingerprintTimeout": true }"""
        const val WEB_FINGERPRINT_TIMEOUT_SECONDS = 10
    }

    override suspend fun handle(
        customerAction: CustomerAction,
        threeDSService: PO3DSService
    ): ProcessOutResult<String> {
        POLogger.info("Handling customer action type: %s", customerAction.rawType)
        return when (customerAction.type()) {
            FINGERPRINT_MOBILE -> fingerprintMobile(
                encodedConfiguration = customerAction.value,
                threeDSService = threeDSService
            )
            CHALLENGE_MOBILE -> challengeMobile(
                encodedChallenge = customerAction.value,
                threeDSService = threeDSService
            )
            FINGERPRINT -> fingerprint(
                url = customerAction.value,
                threeDSService = threeDSService
            )
            REDIRECT, URL -> redirect(
                url = customerAction.value,
                threeDSService = threeDSService
            )
            UNSUPPORTED -> {
                val failure = ProcessOutResult.Failure(
                    code = Internal(),
                    message = "Unsupported 3DS customer action type: ${customerAction.rawType}"
                )
                POLogger.error("%s", failure)
                failure
            }
        }
    }

    private suspend fun fingerprintMobile(
        encodedConfiguration: String,
        threeDSService: PO3DSService
    ): ProcessOutResult<String> =
        suspendCoroutine { continuation ->
            try {
                moshi.adapter(PO3DS2Configuration::class.java)
                    .fromJson(String(Base64.decode(encodedConfiguration, Base64.NO_WRAP)))!!
                    .let { configuration ->
                        threeDSService.authenticationRequest(configuration) { result ->
                            result.onSuccess { authenticationRequest ->
                                val gatewayRequest = GatewayRequest(body = encode(authenticationRequest))
                                val gatewayToken = encode(gatewayRequest)
                                continuation.resume(ProcessOutResult.Success(gatewayToken))
                            }.onFailure { failure ->
                                POLogger.warn("Failed to create authentication request: %s", failure)
                                continuation.resume(failure)
                            }
                        }
                    }
            } catch (e: Exception) {
                val failure = ProcessOutResult.Failure(
                    code = Internal(),
                    message = "Failed to decode configuration: ${e.message}",
                    cause = e
                )
                POLogger.error("%s", failure)
                continuation.resume(failure)
            }
        }

    private suspend fun challengeMobile(
        encodedChallenge: String,
        threeDSService: PO3DSService
    ): ProcessOutResult<String> =
        suspendCoroutine { continuation ->
            try {
                moshi.adapter(PO3DS2Challenge::class.java)
                    .fromJson(String(Base64.decode(encodedChallenge, Base64.NO_WRAP)))!!
                    .let { challenge ->
                        threeDSService.handle(challenge) { result ->
                            result.onSuccess { didSucceed ->
                                val body = if (didSucceed)
                                    CHALLENGE_SUCCESS_GATEWAY_REQUEST_BODY
                                else CHALLENGE_FAILURE_GATEWAY_REQUEST_BODY

                                val gatewayToken = encode(GatewayRequest(body = body))
                                continuation.resume(ProcessOutResult.Success(gatewayToken))
                            }.onFailure { failure ->
                                POLogger.warn("Failed to handle challenge: %s", failure)
                                continuation.resume(failure)
                            }
                        }
                    }
            } catch (e: Exception) {
                val failure = ProcessOutResult.Failure(
                    code = Internal(),
                    message = "Failed to decode challenge: ${e.message}",
                    cause = e
                )
                POLogger.error("%s", failure)
                continuation.resume(failure)
            }
        }

    private suspend fun fingerprint(
        url: String,
        threeDSService: PO3DSService
    ): ProcessOutResult<String> =
        suspendCoroutine { continuation ->
            try {
                threeDSService.handle(
                    PO3DSRedirect(
                        url = java.net.URL(url),
                        timeoutSeconds = WEB_FINGERPRINT_TIMEOUT_SECONDS
                    )
                ) { result ->
                    when (result) {
                        is ProcessOutResult.Success -> continuation.resume(result)
                        is ProcessOutResult.Failure ->
                            if (result.code == Timeout()) {
                                val gatewayRequest = GatewayRequest(
                                    body = WEB_FINGERPRINT_TIMEOUT_GATEWAY_REQUEST_BODY,
                                    url = url
                                )
                                val gatewayToken = encode(gatewayRequest)
                                continuation.resume(ProcessOutResult.Success(gatewayToken))
                            } else {
                                POLogger.warn("Failed to handle URL fingerprint: %s", result)
                                continuation.resume(result)
                            }
                    }
                }
            } catch (e: Exception) {
                val message = when (e) {
                    is MalformedURLException -> "Failed to parse fingerprint URL: $url"
                    else -> "Failed to handle fingerprint with URL: $url"
                }
                val failure = ProcessOutResult.Failure(
                    code = Internal(),
                    message = message,
                    cause = e
                )
                POLogger.error("%s", failure)
                continuation.resume(failure)
            }
        }

    private suspend fun redirect(
        url: String,
        threeDSService: PO3DSService
    ): ProcessOutResult<String> =
        suspendCoroutine { continuation ->
            try {
                threeDSService.handle(
                    PO3DSRedirect(url = java.net.URL(url))
                ) { result ->
                    continuation.resume(result)
                }
            } catch (e: MalformedURLException) {
                val failure = ProcessOutResult.Failure(
                    code = Internal(),
                    message = "Failed to parse redirect URL: $url",
                    cause = e
                )
                POLogger.error("%s", failure)
                continuation.resume(failure)
            }
        }

    private fun encode(request: PO3DS2AuthenticationRequest): String {
        val sdkEphemeralPublicKey = if (request.sdkEphemeralPublicKey.isBlank())
            EphemeralPublicKey()
        else moshi.adapter(EphemeralPublicKey::class.java)
            .fromJson(request.sdkEphemeralPublicKey)!!

        val authenticationRequest = ThreeDS2AuthenticationRequest(
            deviceChannel = DEVICE_CHANNEL,
            sdkAppID = request.sdkAppId,
            sdkEncData = request.deviceData,
            sdkEphemPubKey = sdkEphemeralPublicKey,
            sdkReferenceNumber = request.sdkReferenceNumber,
            sdkTransID = request.sdkTransactionId
        )
        return moshi.adapter(ThreeDS2AuthenticationRequest::class.java).toJson(authenticationRequest)
    }

    private fun encode(gatewayRequest: GatewayRequest): String {
        val bytes = moshi.adapter(GatewayRequest::class.java).toJson(gatewayRequest).toByteArray()
        return GATEWAY_TOKEN_PREFIX + Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    @JsonClass(generateAdapter = true)
    internal data class GatewayRequest(
        val body: String,
        val url: String? = null
    )

    @JsonClass(generateAdapter = true)
    internal data class ThreeDS2AuthenticationRequest(
        val deviceChannel: String,
        val sdkAppID: String,
        val sdkEncData: String,
        val sdkEphemPubKey: EphemeralPublicKey,
        val sdkReferenceNumber: String,
        val sdkTransID: String
    )

    @JsonClass(generateAdapter = true)
    internal data class EphemeralPublicKey(
        val crv: String? = null,
        val kty: String? = null,
        val x: String? = null,
        val y: String? = null
    )
}
