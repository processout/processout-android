package com.processout.sdk.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
internal data class POCustomerTokenResponse(
    val token: POCustomerToken?,
    @Json(name = "customer_action")
    val customerAction: POCustomerAction?
)

/**
 * Customer token information.
 *
 * @param id String value that uniquely identifies this customer's token.
 * @param customerId Customer from which the token was created.
 * @param gatewayConfigurationId Gateway configuration that the token is linked to (which can be empty if unused).
 * @param cardId Card used to create the token.
 * @param invoiceId Invoice used to verify the token.
 * @param type Source used to create the token (which will usually be a Card).
 * @param description Description that will be sent to the tokenization gateway service.
 * @param verificationStatus If you request verification for the token then this field tracks its status.
 * @param isDefault Denotes whether or not this is the customer’s default token (the token used when capturing a payment using the customer’s ID as the source).
 * @param returnUrl For APMs, this is the URL to return to the app after payment is accepted.
 * @param cancelUrl For APMs, this is the URL to return to the app after payment is canceled.
 * @param metadata Metadata related to the token, in the form of key-value pairs (String - String).
 * @param createdAt Date and time when this token was created.
 * @param summary Masked version of the payment details (for example, a card number that shows only the last 4 digits **** **** **** 4242).
 * @param isChargeable Denotes whether or not this token is chargeable.
 * @param manualInvoiceCancellation If true, this lets you refund or void the invoice manually after the token is verified.
 * @param canGetBalance If true then you can find the balance for this token.
 */
@JsonClass(generateAdapter = true)
data class POCustomerToken(
    val id: String,
    @Json(name = "customer_id")
    val customerId: String,
    @Json(name = "gateway_configuration_id")
    val gatewayConfigurationId: String?,
    @Json(name = "card_id")
    val cardId: String?,
    @Json(name = "invoice_id")
    val invoiceId: String?,
    val type: String,
    val description: String?,
    @Json(name = "verification_status")
    val verificationStatus: String,
    @Json(name = "is_default")
    val isDefault: Boolean,
    @Json(name = "return_url")
    val returnUrl: String?,
    @Json(name = "cancel_url")
    val cancelUrl: String?,
    val metadata: Map<String, String>,
    @Json(name = "created_at")
    val createdAt: Date,
    val summary: String?,
    @Json(name = "is_chargeable")
    val isChargeable: Boolean,
    @Json(name = "manual_invoice_cancellation")
    val manualInvoiceCancellation: Boolean?,
    @Json(name = "can_get_balance")
    val canGetBalance: Boolean?
) {
    /**
     * Customer token verification status.
     */
    enum class VerificationStatus(value: String) {
        SUCCESS("success"),
        PENDING("pending"),
        FAILED("failed"),
        NOT_REQUESTED("not-requested"),
        UNKNOWN("unknown")
    }
}
