@file:Suppress("unused")

package com.processout.sdk.core

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

class POFailure private constructor() {

    @JsonClass(generateAdapter = true)
    internal data class ApiError(
        @Json(name = "error_type")
        val errorType: String,
        val message: String?,
        @Json(name = "invalid_fields")
        val invalidFields: List<InvalidField>?
    )

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class InvalidField(
        val name: String,
        val message: String?
    ) : Parcelable

    sealed class Code : Parcelable {
        @Parcelize
        data class Authentication(val authenticationCode: AuthenticationCode) : Code()

        @Parcelize
        data class Validation(val validationCode: ValidationCode) : Code()

        @Parcelize
        data class NotFound(val notFoundCode: NotFoundCode) : Code()

        @Parcelize
        data class Generic(val genericCode: GenericCode) : Code()

        @Parcelize
        object NetworkUnreachable : Code()

        @Parcelize
        object Timeout : Code()

        @Parcelize
        object Cancelled : Code()

        @Parcelize
        object Internal : Code()

        @Parcelize
        object Unknown : Code()
    }

    @Parcelize
    @Suppress("EnumEntryName")
    enum class AuthenticationCode(val rawValue: String) : Parcelable {
        invalid          ("request.authentication.invalid"),
        invalidProjectId ("request.authentication.invalid-project-id")
    }

    @Parcelize
    @Suppress("EnumEntryName")
    enum class ValidationCode(val rawValue: String) : Parcelable {
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

    @Parcelize
    @Suppress("EnumEntryName")
    enum class NotFoundCode(val rawValue: String) : Parcelable {
        activity                  ("resource.activity.not-found"),
        addon                     ("resource.addon.not-found"),
        alert                     ("resource.alert.not-found"),
        apiKey                    ("resource.api-key.not-found"),
        apiRequest                ("resource.api-request.not-found"),
        apiVersion                ("resource.api-version.not-found"),
        board                     ("resource.board.not-found"),
        card                      ("resource.card.not-found"),
        chart                     ("resource.chart.not-found"),
        collaborator              ("resource.collaborator.not-found"),
        country                   ("resource.country.not-found"),
        coupon                    ("resource.coupon.not-found"),
        currency                  ("resource.currency.not-found"),
        customer                  ("resource.customer.not-found"),
        discount                  ("resource.discount.not-found"),
        event                     ("resource.event.not-found"),
        export                    ("resource.export.not-found"),
        fraudServiceConfiguration ("resource.fraud-service-configuration.not-found"),
        gateway                   ("resource.gateway.not-found"),
        gatewayConfiguration      ("resource.gateway-configuration.not-found"),
        general                   ("resource.not-found"),
        invoice                   ("resource.invoice.not-found"),
        payout                    ("resource.payout.not-found"),
        permissionGroup           ("resource.permission-group.not-found"),
        plan                      ("resource.plan.not-found"),
        product                   ("resource.product.not-found"),
        project                   ("resource.project.not-found"),
        refund                    ("resource.refund.not-found"),
        route                     ("request.route-not-found"),
        subscription              ("resource.subscription.not-found"),
        token                     ("resource.token.not-found"),
        tokenizationRequest       ("resource.tokenization-request.not-found"),
        transaction               ("resource.transaction.not-found"),
        user                      ("resource.user.not-found"),
        webhookEndpoint           ("resource.webhook-endpoint.not-found")
    }

    @Parcelize
    @Suppress("EnumEntryName")
    enum class GenericCode(val rawValue: String) : Parcelable {
        gatewayDeclined                     ("gateway.declined"),
        requestBadFormat                    ("request.bad-format"),
        requestCardAlreadyUsed              ("request.source.card-already-used"),
        requestGatewayNotAvailable          ("request.gateway.not-available"),
        requestGatewayOperationNotSupported ("request.gateway.operation-not-supported"),
        requestInvalidCard                  ("request.card.invalid"),
        requestInvalidExpand                ("request.expand.invalid"),
        requestInvalidFilter                ("request.filter.invalid"),
        requestInvalidIdempotency           ("request.idempotency-key.invalid"),
        requestInvalidPagination            ("request.pagination.invalid"),
        requestInvalidSource                ("request.source.invalid"),
        requestNoGatewayConfiguration       ("request.configuration.missing-gateway-configuration"),
        requestRateExceeded                 ("request.rate.exceeded"),
        requestStillProcessing              ("request.still-processing"),
        requestTooMuch                      ("request.too-much"),
        resourceNotLinked                   ("resource.not-linked"),
        routingRulesTransactionBlocked      ("routing-rules.transaction-blocked"),
        sandboxNotSupported                 ("sandbox.not-supported"),
        serviceNotSupported                 ("service.not-supported")
    }
}

val POFailure.Code.rawValue: String
    get() = when (this) {
        is POFailure.Code.Authentication -> authenticationCode.rawValue
        is POFailure.Code.Validation -> validationCode.rawValue
        is POFailure.Code.NotFound -> notFoundCode.rawValue
        is POFailure.Code.Generic -> genericCode.rawValue
        POFailure.Code.NetworkUnreachable -> "processout-mobile.network-unreachable"
        POFailure.Code.Timeout -> "processout-mobile.timeout"
        POFailure.Code.Cancelled -> "processout-mobile.cancelled"
        POFailure.Code.Internal -> "processout-mobile.internal"
        POFailure.Code.Unknown -> "processout-mobile.unknown"
    }
