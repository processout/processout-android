package com.processout.sdk.api.model.response

import com.processout.sdk.utils.findBy
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.net.URL

@JsonClass(generateAdapter = true)
internal data class POCustomerAction(
    @Json(name = "type")
    val rawType: String,
    val value: String
) {
    fun type() = Type::rawType.findBy(rawType) ?: Type.UNSUPPORTED

    enum class Type(val rawType: String) {
        FINGERPRINT_MOBILE("fingerprint-mobile"),
        CHALLENGE_MOBILE("challenge-mobile"),
        FINGERPRINT("fingerprint"),
        REDIRECT("redirect"),
        URL("url"),
        UNSUPPORTED(String())
    }
}

/**
 * Represents the configuration parameters that are required by the 3DS SDK for initialization.
 *
 * @param directoryServerId The identifier of the directory server to use during the transaction creation phase.
 * @param directoryServerPublicKey The public key of the directory server to use during the transaction creation phase.
 * @param directoryServerTransactionId Unique identifier for the authentication assigned by the DS (Card Scheme).
 * @param directoryServerRootCAs List of directory server root CAs.
 * @param messageVersion 3DS protocol version identifier.
 * @param scheme Optional directory server scheme.
 */
@JsonClass(generateAdapter = true)
data class PO3DS2Configuration(
    @Json(name = "directoryServerID")
    val directoryServerId: String,
    val directoryServerPublicKey: String,
    @Json(name = "threeDSServerTransID")
    val directoryServerTransactionId: String,
    val directoryServerRootCAs: List<String>,
    val messageVersion: String,
    val scheme: String?
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
 * @param url Redirect URL.
 * @param isHeadlessModeAllowed Boolean value that indicates whether a given URL can be handled in headless mode, meaning without showing any UI for the user.
 * @param timeoutSeconds Optional timeout interval in seconds.
 */
data class PO3DSRedirect(
    val url: URL,
    val isHeadlessModeAllowed: Boolean,
    val timeoutSeconds: Int? = null
)
