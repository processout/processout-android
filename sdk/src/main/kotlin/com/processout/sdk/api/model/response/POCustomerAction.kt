package com.processout.sdk.api.model.response

import android.net.Uri
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

sealed class POCustomerActionResponse {
    data class UriData(val value: Uri) : POCustomerActionResponse()
    data class AuthenticationChallengeData(
        val value: PO3DS2Challenge
    ) : POCustomerActionResponse()

    data class AuthenticationFingerprintData(
        val value: PO3DS2Configuration
    ) : POCustomerActionResponse()
}

data class POInvoiceAuthorizationSuccess(
    val customerAction: POCustomerActionResponse?
)

@JsonClass(generateAdapter = true)
internal data class POInvoiceAuthorizationResponse(
    @Json(name = "customer_action")
    val customerAction: POCustomerAction? = null
)

@JsonClass(generateAdapter = true)
internal data class POCustomerAction(
    val type: String,
    val value: String
)

/**
 * Information from the 3DS Server authentication response that could be used by the 3DS2 SDK to initiate the challenge flow.
 *
 * @param acsTransactionId Unique transaction identifier assigned by the ACS.
 * @param acsReferenceNumber Unique identifier that identifies the ACS service provider.
 * @param acsSignedContent The encrypted message containing the ACS information (including Ephemeral Public Key) and the 3DS2 SDK ephemeral public key.
 * @param threeDSServerTransactionId Unique identifier for the authentication assigned by the DS (Card Scheme).
 */
@JsonClass(generateAdapter = true)
data class PO3DS2Challenge(
    @Json(name = "acsTransID")
    val acsTransactionId: String,
    val acsReferenceNumber: String,
    val acsSignedContent: String,
    @Json(name = "threeDSServerTransID")
    val threeDSServerTransactionId: String
)

/**
 * Represents the configuration parameters that are required by the 3DS SDK for initialization.
 *
 * @param directoryServerId The identifier of the directory server to use during the transaction creation phase.
 * @param directoryServerPublicKey The public key of the directory server to use during the transaction creation phase.
 * @param directoryServerTransactionId Unique identifier for the authentication assigned by the DS (Card Scheme).
 * @param messageVersion 3DS protocol version identifier.
 */
@JsonClass(generateAdapter = true)
data class PO3DS2Configuration(
    @Json(name = "directoryServerID")
    val directoryServerId: String,
    val directoryServerPublicKey: String,
    @Json(name = "threeDSServerTransID")
    val directoryServerTransactionId: String,
    val messageVersion: String
)

enum class CustomerActionType(val value: String) {
    FINGERPRINT_MOBILE("fingerprint-mobile"),
    CHALLENGE_MOBILE("challenge-mobile"),
    URL("url"),
    REDIRECT("redirect"),
    FINGERPRINT("fingerprint")
}
