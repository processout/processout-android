package com.processout.sdk.api.model.response

import android.net.Uri
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// TODO: check usages in Invoices and CustomerTokens after refactoring,
// TODO: maybe it can be 'internal'
sealed class PO3DSCustomerAction {
    data class FingerprintMobile(
        val value: PO3DS2Configuration
    ) : PO3DSCustomerAction()

    data class ChallengeMobile(
        val value: PO3DS2Challenge
    ) : PO3DSCustomerAction()

    data class Fingerprint(val value: Uri) : PO3DSCustomerAction()
    data class Redirect(val value: Uri) : PO3DSCustomerAction()
}

data class POInvoiceAuthorizationSuccess(
    val customerAction: PO3DSCustomerAction?
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

/**
 * @param uri Redirect URI.
 * @param isHeadlessModeAllowed Boolean value that indicates whether a given URL can be handled in headless mode, meaning without showing any UI for the user.
 * @param timeoutSeconds Optional timeout interval in seconds.
 */
data class PO3DSRedirect(
    val uri: Uri,
    val isHeadlessModeAllowed: Boolean,
    val timeoutSeconds: Int? = null
)

enum class CustomerActionType(val value: String) {
    FINGERPRINT_MOBILE("fingerprint-mobile"),
    CHALLENGE_MOBILE("challenge-mobile"),
    URL("url"),
    REDIRECT("redirect"),
    FINGERPRINT("fingerprint")
}
