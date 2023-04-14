package com.processout.sdk.api.model.threeds

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Represents the configuration parameters that are required by the 3DS SDK for initialization.
 *
 * @param directoryServerId The identifier of the directory server to use during the transaction creation phase.
 * @param directoryServerPublicKey The public key of the directory server to use during the transaction creation phase.
 * @param directoryServerTransactionId Unique identifier for the authentication assigned by the DS (Card Scheme).
 * @param directoryServerRootCertificates List of DER-encoded x509 certificate strings containing the DS root certificate used for signature checks.
 * @param messageVersion 3DS protocol version identifier.
 * @param scheme Card scheme from the card used to initiate the payment.
 */
@JsonClass(generateAdapter = true)
data class PO3DS2Configuration(
    @Json(name = "directoryServerID")
    val directoryServerId: String,
    val directoryServerPublicKey: String,
    @Json(name = "threeDSServerTransID")
    val directoryServerTransactionId: String,
    @Json(name = "directoryServerRootCAs")
    val directoryServerRootCertificates: List<String>,
    val messageVersion: String,
    val scheme: String?
)
