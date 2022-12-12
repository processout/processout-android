package com.processout.sdk.api.network.exception

import com.processout.sdk.utils.findBy
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiError(
    @Json(name = "error_type")
    val errorType: String,
    val message: String?,
    @Json(name = "invalid_fields")
    val invalidFields: List<InvalidField>?
) {

    @JsonClass(generateAdapter = true)
    data class InvalidField(
        val name: String,
        val message: String?
    )

    fun validationError() = ValidationError::errorType findBy errorType

    @Suppress("EnumEntryName")
    enum class ValidationError(val errorType: String) {
        general                   ("request.validation.error"),
        invalidAddress            ("request.validation.invalid-address"),
        invalidAmount             ("request.validation.invalid-amount"),
        invalidChallengeIndicator ("request.validation.invalid-challenge-indicator"),
        invalidCountry            ("request.validation.invalid-country"),
        invalidCurrency           ("request.validation.invalid-currency"),
        invalidCustomerInput      ("gateway.invalid-customer-input"),
        invalidDate               ("request.validation.invalid-date"),
        invalidDescription        ("request.validation.invalid-description"),
        invalidDetailCategory     ("request.validation.invalid-detail-category"),
        invalidDetailCondition    ("request.validation.invalid-detail-condition"),
        invalidDeviceChannel      ("request.validation.invalid-device-channel"),
        invalidDuration           ("request.validation.invalid-duration"),
        invalidEmail              ("request.validation.invalid-email"),
        invalidExemptionReason    ("request.validation.invalid-exemption-reason"),
        invalidExternalFraudTools ("request.validation.invalid-external-fraud-tools"),
        invalidGatewayData        ("request.validation.invalid-gateway-data"),
        invalidId                 ("request.validation.invalid-id"),
        invalidIpAddress          ("request.validation.invalid-ip-address"),
        invalidLegalDocument      ("request.validation.invalid-legal-document"),
        invalidMetadata           ("request.validation.invalid-metadata"),
        invalidName               ("request.validation.invalid-name"),
        invalidPaymentType        ("request.validation.invalid-payment-type"),
        invalidPercent            ("request.validation.invalid-percent"),
        invalidPhoneNumber        ("request.validation.invalid-phone-number"),
        invalidQuantity           ("request.validation.invalid-quantity"),
        invalidRelationship       ("request.validation.invalid-relationship"),
        invalidRelayStoreName     ("request.validation.invalid-relay-store-name"),
        invalidRole               ("request.validation.invalid-role"),
        invalidSettings           ("request.validation.invalid-settings"),
        invalidSex                ("request.validation.invalid-sex"),
        invalidShippingDelay      ("request.validation.invalid-shipping-delay"),
        invalidShippingMethod     ("request.validation.invalid-shipping-method"),
        invalidState              ("gateway.invalid-state"),
        invalidSubAccount         ("request.validation.invalid-subaccount"),
        invalidTaxAmount          ("request.validation.invalid-tax-amount"),
        invalidTaxRate            ("request.validation.invalid-tax-rate"),
        invalidType               ("request.validation.invalid-type"),
        invalidUrl                ("request.validation.invalid-url"),
        invalidUser               ("request.validation.invalid-user"),
        missingCurrency           ("request.validation.missing-currency"),
        missingCustomerInput      ("gateway.missing-customer-input"),
        missingDescription        ("request.validation.missing-description"),
        missingEmail              ("request.validation.missing-email"),
        missingInvoice            ("request.validation.missing-invoice"),
        missingName               ("request.validation.missing-name"),
        missingSource             ("request.validation.missing-source"),
        missingType               ("request.validation.missing-type")
    }
}
