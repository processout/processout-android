package com.processout.sdk.api.service

import android.util.Base64
import com.processout.sdk.api.model.response.POCustomerAction
import com.processout.sdk.api.model.response.POCustomerAction.Type.*
import com.processout.sdk.api.model.threeds.PO3DS2AuthenticationRequest
import com.processout.sdk.api.model.threeds.PO3DS2Challenge
import com.processout.sdk.api.model.threeds.PO3DS2Configuration
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.core.POFailure
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
        action: POCustomerAction,
        threeDSHandler: PO3DSHandler,
        callback: (PO3DSResult<String>) -> Unit
    ) {
        when (action.type()) {
            FINGERPRINT_MOBILE -> fingerprintMobile(
                encodedConfiguration = action.value, threeDSHandler, callback
            )
            CHALLENGE_MOBILE -> challengeMobile(
                encodedChallenge = action.value, threeDSHandler, callback
            )
            FINGERPRINT -> fingerprint(url = action.value, threeDSHandler, callback)
            REDIRECT, URL -> redirect(url = action.value, threeDSHandler, callback)
            UNSUPPORTED -> callback(
                PO3DSResult.Failure(
                    POFailure.Code.Internal(),
                    "Unsupported 3DS customer action type: ${action.rawType}"
                )
            )
        }
    }

    private fun fingerprintMobile(
        encodedConfiguration: String,
        threeDSHandler: PO3DSHandler,
        callback: (PO3DSResult<String>) -> Unit
    ) {
        try {
            moshi.adapter(PO3DS2Configuration::class.java)
                .fromJson(String(Base64.decode(encodedConfiguration, Base64.NO_WRAP)))!!
                .let { configuration ->
                    threeDSHandler.authenticationRequest(configuration) { result ->
                        when (result) {
                            is PO3DSResult.Success -> callback(
                                ChallengeResponse(body = encode(result.value)), callback
                            )
                            is PO3DSResult.Failure -> callback(result.copy())
                        }
                    }
                }
        } catch (e: Exception) {
            callback(
                PO3DSResult.Failure(
                    POFailure.Code.Internal(),
                    "Failed to decode configuration: ${e.message}", e
                )
            )
        }
    }

    private fun challengeMobile(
        encodedChallenge: String,
        threeDSHandler: PO3DSHandler,
        callback: (PO3DSResult<String>) -> Unit
    ) {
        try {
            moshi.adapter(PO3DS2Challenge::class.java)
                .fromJson(String(Base64.decode(encodedChallenge, Base64.NO_WRAP)))!!
                .let { challenge ->
                    threeDSHandler.handle(challenge) { result ->
                        when (result) {
                            is PO3DSResult.Success -> {
                                val body = if (result.value)
                                    CHALLENGE_SUCCESS_RESPONSE_BODY
                                else CHALLENGE_FAILURE_RESPONSE_BODY
                                callback(ChallengeResponse(body = body), callback)
                            }
                            is PO3DSResult.Failure -> callback(result.copy())
                        }
                    }
                }
        } catch (e: Exception) {
            callback(
                PO3DSResult.Failure(
                    POFailure.Code.Internal(),
                    "Failed to decode challenge: ${e.message}", e
                )
            )
        }
    }

    private fun fingerprint(
        url: String,
        threeDSHandler: PO3DSHandler,
        callback: (PO3DSResult<String>) -> Unit
    ) {
        try {
            threeDSHandler.handle(
                PO3DSRedirect(
                    url = java.net.URL(url),
                    isHeadlessModeAllowed = true,
                    timeoutSeconds = WEB_FINGERPRINT_TIMEOUT_SECONDS
                )
            ) { result ->
                when (result) {
                    is PO3DSResult.Success -> callback(result.copy())
                    is PO3DSResult.Failure ->
                        when (result.code == POFailure.Code.Timeout()) {
                            true -> callback(
                                ChallengeResponse(
                                    body = WEB_FINGERPRINT_TIMEOUT_RESPONSE_BODY,
                                    url = url
                                ), callback
                            )
                            false -> callback(result.copy())
                        }
                }
            }
        } catch (e: Exception) {
            when (e) {
                is MalformedURLException -> callback(
                    PO3DSResult.Failure(
                        POFailure.Code.Internal(),
                        "Failed to parse fingerprint URL from raw value: $url", e
                    )
                )
                else -> callback(
                    PO3DSResult.Failure(
                        POFailure.Code.Internal(),
                        "Failed to handle fingerprint for URL: $url", e
                    )
                )
            }
        }
    }

    private fun redirect(
        url: String,
        threeDSHandler: PO3DSHandler,
        callback: (PO3DSResult<String>) -> Unit
    ) {
        try {
            threeDSHandler.handle(
                PO3DSRedirect(
                    url = java.net.URL(url),
                    isHeadlessModeAllowed = false
                ), callback
            )
        } catch (e: MalformedURLException) {
            callback(
                PO3DSResult.Failure(
                    POFailure.Code.Internal(),
                    "Failed to parse redirect URL from raw value: $url", e
                )
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

    private fun callback(response: ChallengeResponse, callback: (PO3DSResult<String>) -> Unit) {
        val bytes = moshi.adapter(ChallengeResponse::class.java).toJson(response).toByteArray()
        val token = TOKEN_PREFIX + Base64.encodeToString(bytes, Base64.NO_WRAP)
        callback(PO3DSResult.Success(token))
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
