package com.processout.sdk.api.model.request

/**
 * Holds transaction data that the 3DS Server requires to create the AReq.
 *
 * @param deviceData Encrypted device data as a JWE string.
 * @param sdkAppId A unique string identifying the application.
 * @param sdkEphemeralPublicKey The public key component of the ephemeral keypair generated for the transaction, represented as a JWK string.
 * @param sdkReferenceNumber A string identifying the SDK, assigned by EMVCo.
 * @param sdkTransactionId A unique string identifying the transaction within the scope of the SDK.
 */
data class PO3DS2AuthenticationRequest(
    val deviceData: String,
    val sdkAppId: String,
    val sdkEphemeralPublicKey: String,
    val sdkReferenceNumber: String,
    val sdkTransactionId: String
)
