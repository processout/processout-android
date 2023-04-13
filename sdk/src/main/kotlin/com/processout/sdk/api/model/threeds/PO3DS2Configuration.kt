package com.processout.sdk.api.model.threeds

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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
