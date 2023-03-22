package com.processout.sdk.api.model.request

import android.net.Uri
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

sealed class POCustomerActionResponse {
    data class UriData(val value: Uri) : POCustomerActionResponse()
    data class AuthenticationChallengeData(
        val value: POAuthenticationChallengeData
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

@JsonClass(generateAdapter = true)
data class POAuthenticationChallengeData(
    val acsTransID: String,
    val acsReferenceNumber: String,
    val acsSignedContent: String,
    val threeDSServerTransID: String
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
