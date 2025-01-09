package com.processout.sdk.api.model.request

/**
 * Request to delete a customer token.
 *
 * @param[customerId] ID of the customer.
 * @param[tokenId] Token ID that belong to the customer.
 * @param[clientSecret] Client secret is a value of __X-ProcessOut-Client-Secret__ header of the invoice.
 */
data class PODeleteCustomerTokenRequest(
    val customerId: String,
    val tokenId: String,
    val clientSecret: String
)
