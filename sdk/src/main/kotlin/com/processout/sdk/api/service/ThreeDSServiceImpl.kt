package com.processout.sdk.api.service

import android.util.Base64
import com.processout.sdk.api.model.response.CustomerAction
import com.processout.sdk.api.model.response.CustomerAction.Type.*
import com.processout.sdk.api.model.threeds.*
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import java.net.MalformedURLException

internal class ThreeDSServiceImpl(private val moshi: Moshi) : ThreeDSService {

    private companion object {
        private const val DEVICE_CHANNEL = "app"
        private const val TOKEN_PREFIX = "gway_req_"
        private const val CHALLENGE_SUCCESS_RESPONSE_BODY = """{ "transStatus": "Y" }"""
        private const val CHALLENGE_FAILURE_RESPONSE_BODY = """{ "transStatus": "N" }"""
        private const val WEB_FINGERPRINT_TIMEOUT_RESPONSE_BODY = """{ "threeDS2FingerprintTimeout": true }"""
        private const val WEB_FINGERPRINT_TIMEOUT_SECONDS = 10
    }

    override fun handle(
        action: CustomerAction,
        delegate: PO3DSService,
        callback: (ProcessOutResult<String>) -> Unit
    ) {
        POLogger.info("Handling customer action type: %s", action.rawType)
        when (action.type()) {
            FINGERPRINT_MOBILE -> fingerprintMobile(
                encodedConfiguration = action.value, delegate, callback
            )
            CHALLENGE_MOBILE -> challengeMobile(
                encodedChallenge = action.value, delegate, callback
            )
            FINGERPRINT -> fingerprint(url = action.value, delegate, callback)
            REDIRECT, URL -> redirect(url = action.value, delegate, callback)
            UNSUPPORTED -> callback(
                ProcessOutResult.Failure(
                    POFailure.Code.Internal(),
                    "Unsupported 3DS customer action type: ${action.rawType}"
                ).also { POLogger.error("%s", it) }
            )
        }
    }

    private fun fingerprintMobile(
        encodedConfiguration: String,
        delegate: PO3DSService,
        callback: (ProcessOutResult<String>) -> Unit
    ) {
        try {
            moshi.adapter(PO3DS2Configuration::class.java)
                .fromJson(String(Base64.decode(encodedConfiguration, Base64.NO_WRAP)))!!
                .let { configuration ->
                    delegate.authenticationRequest(configuration) { result ->
                        when (result) {
                            is ProcessOutResult.Success -> callback(
                                ChallengeResponse(body = encode(result.value)), callback
                            )
                            is ProcessOutResult.Failure -> callback(result.copy()
                                .also { POLogger.info("Failed to create authentication request: %s", it) }
                            )
                        }
                    }
                }
        } catch (e: Exception) {
            callback(
                ProcessOutResult.Failure(
                    POFailure.Code.Internal(),
                    "Failed to decode configuration: ${e.message}", cause = e
                ).also { POLogger.error("%s", it) }
            )
        }
    }

    private fun challengeMobile(
        encodedChallenge: String,
        delegate: PO3DSService,
        callback: (ProcessOutResult<String>) -> Unit
    ) {
        try {
            moshi.adapter(PO3DS2Challenge::class.java)
                .fromJson(String(Base64.decode(encodedChallenge, Base64.NO_WRAP)))!!
                .let { challenge ->
                    delegate.handle(challenge) { result ->
                        when (result) {
                            is ProcessOutResult.Success -> {
                                val body = if (result.value)
                                    CHALLENGE_SUCCESS_RESPONSE_BODY
                                else CHALLENGE_FAILURE_RESPONSE_BODY
                                callback(ChallengeResponse(body = body), callback)
                            }
                            is ProcessOutResult.Failure -> callback(result.copy()
                                .also { POLogger.info("Failed to handle challenge: %s", it) }
                            )
                        }
                    }
                }
        } catch (e: Exception) {
            callback(
                ProcessOutResult.Failure(
                    POFailure.Code.Internal(),
                    "Failed to decode challenge: ${e.message}", cause = e
                ).also { POLogger.error("%s", it) }
            )
        }
    }

    private fun fingerprint(
        url: String,
        delegate: PO3DSService,
        callback: (ProcessOutResult<String>) -> Unit
    ) {
        try {
            delegate.handle(
                PO3DSRedirect(
                    url = java.net.URL(url),
                    timeoutSeconds = WEB_FINGERPRINT_TIMEOUT_SECONDS
                )
            ) { result ->
                when (result) {
                    is ProcessOutResult.Success -> callback(result.copy())
                    is ProcessOutResult.Failure ->
                        when (result.code == POFailure.Code.Timeout()) {
                            true -> callback(
                                ChallengeResponse(
                                    body = WEB_FINGERPRINT_TIMEOUT_RESPONSE_BODY,
                                    url = url
                                ), callback
                            )
                            false -> callback(result.copy()
                                .also { POLogger.info("Failed to handle URL fingerprint: %s", it) }
                            )
                        }
                }
            }
        } catch (e: Exception) {
            when (e) {
                is MalformedURLException -> callback(
                    ProcessOutResult.Failure(
                        POFailure.Code.Internal(),
                        "Failed to parse fingerprint URL: $url", cause = e
                    ).also { POLogger.error("%s", it) }
                )
                else -> callback(
                    ProcessOutResult.Failure(
                        POFailure.Code.Internal(),
                        "Failed to handle fingerprint with URL: $url", cause = e
                    ).also { POLogger.error("%s", it) }
                )
            }
        }
    }

    private fun redirect(
        url: String,
        delegate: PO3DSService,
        callback: (ProcessOutResult<String>) -> Unit
    ) {
        try {
            delegate.handle(PO3DSRedirect(url = java.net.URL(url)), callback)
        } catch (e: MalformedURLException) {
            callback(
                ProcessOutResult.Failure(
                    POFailure.Code.Internal(),
                    "Failed to parse redirect URL: $url", cause = e
                ).also { POLogger.error("%s", it) }
            )
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

    private fun callback(response: ChallengeResponse, callback: (ProcessOutResult<String>) -> Unit) {
        val bytes = moshi.adapter(ChallengeResponse::class.java).toJson(response).toByteArray()
        val token = TOKEN_PREFIX + Base64.encodeToString(bytes, Base64.NO_WRAP)
        callback(ProcessOutResult.Success(token))
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
