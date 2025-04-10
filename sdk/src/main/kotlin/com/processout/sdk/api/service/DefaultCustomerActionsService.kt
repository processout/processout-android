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
        const val TOKEN_PREFIX = "gway_req_"
        const val CHALLENGE_SUCCESS_RESPONSE_BODY = """{ "transStatus": "Y" }"""
        const val CHALLENGE_FAILURE_RESPONSE_BODY = """{ "transStatus": "N" }"""
        const val WEB_FINGERPRINT_TIMEOUT_RESPONSE_BODY = """{ "threeDS2FingerprintTimeout": true }"""
        const val WEB_FINGERPRINT_TIMEOUT_SECONDS = 10
    }

    override suspend fun handle(
        action: CustomerAction,
        threeDSService: PO3DSService
    ): ProcessOutResult<String> {
        POLogger.info("Handling customer action type: %s", action.rawType)
        return when (action.type()) {
            FINGERPRINT_MOBILE -> fingerprintMobile(
                encodedConfiguration = action.value,
                threeDSService = threeDSService
            )
            CHALLENGE_MOBILE -> challengeMobile(
                encodedChallenge = action.value,
                threeDSService = threeDSService
            )
            FINGERPRINT -> fingerprint(
                url = action.value,
                threeDSService = threeDSService
            )
            REDIRECT, URL -> redirect(
                url = action.value,
                threeDSService = threeDSService
            )
            UNSUPPORTED -> {
                val failure = ProcessOutResult.Failure(
                    code = Internal(),
                    message = "Unsupported 3DS customer action type: ${action.rawType}"
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
                            when (result) {
                                is ProcessOutResult.Success -> {
                                    val token = encode(ChallengeResponse(body = encode(result.value)))
                                    continuation.resume(ProcessOutResult.Success(token))
                                }
                                is ProcessOutResult.Failure -> {
                                    POLogger.warn("Failed to create authentication request: %s", result)
                                    continuation.resume(result)
                                }
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
                            when (result) {
                                is ProcessOutResult.Success -> {
                                    val body = if (result.value)
                                        CHALLENGE_SUCCESS_RESPONSE_BODY
                                    else CHALLENGE_FAILURE_RESPONSE_BODY
                                    val token = encode(ChallengeResponse(body = body))
                                    continuation.resume(ProcessOutResult.Success(token))
                                }
                                is ProcessOutResult.Failure -> {
                                    POLogger.warn("Failed to handle challenge: %s", result)
                                    continuation.resume(result)
                                }
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
                            when (result.code == Timeout()) {
                                true -> {
                                    val token = encode(
                                        ChallengeResponse(
                                            body = WEB_FINGERPRINT_TIMEOUT_RESPONSE_BODY,
                                            url = url
                                        )
                                    )
                                    continuation.resume(ProcessOutResult.Success(token))
                                }
                                false -> {
                                    POLogger.warn("Failed to handle URL fingerprint: %s", result)
                                    continuation.resume(result)
                                }
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

        val authRequest = ThreeDS2AuthenticationRequest(
            deviceChannel = DEVICE_CHANNEL,
            sdkAppID = request.sdkAppId,
            sdkEncData = request.deviceData,
            sdkEphemPubKey = sdkEphemeralPublicKey,
            sdkReferenceNumber = request.sdkReferenceNumber,
            sdkTransID = request.sdkTransactionId
        )
        return moshi.adapter(ThreeDS2AuthenticationRequest::class.java).toJson(authRequest)
    }

    private fun encode(response: ChallengeResponse): String {
        val bytes = moshi.adapter(ChallengeResponse::class.java).toJson(response).toByteArray()
        return TOKEN_PREFIX + Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    @JsonClass(generateAdapter = true)
    internal data class ChallengeResponse(
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
