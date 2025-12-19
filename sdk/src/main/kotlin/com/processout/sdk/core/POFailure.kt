@file:Suppress("unused")

package com.processout.sdk.core

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * Defines error codes structure.
 */
class POFailure private constructor() {

    @JsonClass(generateAdapter = true)
    internal data class ApiError(
        @Json(name = "error_type")
        val errorType: String,
        val message: String?,
        @Json(name = "invalid_fields")
        val invalidFields: List<InvalidField>?
    )

    /**
     * Defines invalid field details.
     */
    @Parcelize
    @JsonClass(generateAdapter = true)
    data class InvalidField(
        val name: String,
        val message: String?
    ) : Parcelable

    /**
     * Base code type.
     */
    sealed class Code : Parcelable {
        /**
         * API credentials could not be verified.
         */
        @Parcelize
        data class Authentication(val authenticationCode: AuthenticationCode) : Code()

        /**
         * Request data is not valid or cannot be validated.
         */
        @Parcelize
        data class Validation(val validationCode: ValidationCode) : Code()

        /**
         * The requested resource could not be found.
         */
        @Parcelize
        data class NotFound(val notFoundCode: NotFoundCode) : Code()

        /**
         * Generic error that can’t be classified as specific.
         */
        @Parcelize
        data class Generic(val genericCode: GenericCode = GenericCode.mobile) : Code()

        /**
         * Operation is cancelled.
         */
        @Parcelize
        data object Cancelled : Code()

        /**
         * No network connection.
         */
        @Parcelize
        data object NetworkUnreachable : Code()

        /**
         * Operation is timed out.
         */
        @Parcelize
        data class Timeout(val timeoutCode: TimeoutCode = TimeoutCode.mobile) : Code()

        /**
         * Something went wrong on the ProcessOut side. This is extremely rare.
         */
        @Parcelize
        data class Internal(val internalCode: InternalCode = InternalCode.mobile) : Code()

        /**
         * Unknown error that can’t be interpreted. Inspect associated [rawValue] for additional details.
         */
        @Parcelize
        data class Unknown(val rawValue: String) : Code()
    }

    /**
     * API credentials could not be verified.
     */
    @Parcelize
    @Suppress("EnumEntryName")
    enum class AuthenticationCode(val rawValue: String) : Parcelable {
        invalid("request.authentication.invalid"),
        invalidProjectId("request.authentication.invalid-project-id")
    }

    /**
     * Request data is not valid or cannot be validated.
     */
    @Parcelize
    @Suppress("EnumEntryName")
    enum class ValidationCode(val rawValue: String) : Parcelable {
        general("request.validation.error"),
        gateway("gateway.validation-error"),
        invalidAddress("request.validation.invalid-address"),
        invalidAmount("request.validation.invalid-amount"),
        invalidChallengeIndicator("request.validation.invalid-challenge-indicator"),
        invalidCountry("request.validation.invalid-country"),
        invalidCurrency("request.validation.invalid-currency"),
        invalidCustomerInput("gateway.invalid-customer-input"),
        invalidDate("request.validation.invalid-date"),
        invalidDescription("request.validation.invalid-description"),
        invalidDetailCategory("request.validation.invalid-detail-category"),
        invalidDetailCondition("request.validation.invalid-detail-condition"),
        invalidDeviceChannel("request.validation.invalid-device-channel"),
        invalidDuration("request.validation.invalid-duration"),
        invalidEmail("request.validation.invalid-email"),
        invalidExemptionReason("request.validation.invalid-exemption-reason"),
        invalidExternalFraudTools("request.validation.invalid-external-fraud-tools"),
        invalidGatewayData("request.validation.invalid-gateway-data"),
        invalidId("request.validation.invalid-id"),
        invalidIpAddress("request.validation.invalid-ip-address"),
        invalidLegalDocument("request.validation.invalid-legal-document"),
        invalidMetadata("request.validation.invalid-metadata"),
        invalidName("request.validation.invalid-name"),
        invalidPaymentType("request.validation.invalid-payment-type"),
        invalidPercent("request.validation.invalid-percent"),
        invalidPhoneNumber("request.validation.invalid-phone-number"),
        invalidQuantity("request.validation.invalid-quantity"),
        invalidRelationship("request.validation.invalid-relationship"),
        invalidRelayStoreName("request.validation.invalid-relay-store-name"),
        invalidRole("request.validation.invalid-role"),
        invalidSettings("request.validation.invalid-settings"),
        invalidSex("request.validation.invalid-sex"),
        invalidShippingDelay("request.validation.invalid-shipping-delay"),
        invalidShippingMethod("request.validation.invalid-shipping-method"),
        invalidState("gateway.invalid-state"),
        invalidSubAccount("request.validation.invalid-subaccount"),
        invalidTaxAmount("request.validation.invalid-tax-amount"),
        invalidTaxRate("request.validation.invalid-tax-rate"),
        invalidType("request.validation.invalid-type"),
        invalidUrl("request.validation.invalid-url"),
        invalidUser("request.validation.invalid-user"),
        missingCurrency("request.validation.missing-currency"),
        missingCustomerInput("gateway.missing-customer-input"),
        missingDescription("request.validation.missing-description"),
        missingEmail("request.validation.missing-email"),
        missingInvoice("request.validation.missing-invoice"),
        missingName("request.validation.missing-name"),
        missingSource("request.validation.missing-source"),
        missingType("request.validation.missing-type")
    }

    /**
     * The requested resource could not be found.
     */
    @Parcelize
    @Suppress("EnumEntryName")
    enum class NotFoundCode(val rawValue: String) : Parcelable {
        activity("resource.activity.not-found"),
        addon("resource.addon.not-found"),
        alert("resource.alert.not-found"),
        apiKey("resource.api-key.not-found"),
        apiRequest("resource.api-request.not-found"),
        apiVersion("resource.api-version.not-found"),
        board("resource.board.not-found"),
        card("resource.card.not-found"),
        chart("resource.chart.not-found"),
        collaborator("resource.collaborator.not-found"),
        country("resource.country.not-found"),
        coupon("resource.coupon.not-found"),
        currency("resource.currency.not-found"),
        customer("resource.customer.not-found"),
        discount("resource.discount.not-found"),
        event("resource.event.not-found"),
        export("resource.export.not-found"),
        fraudServiceConfiguration("resource.fraud-service-configuration.not-found"),
        gateway("resource.gateway.not-found"),
        gatewayConfiguration("resource.gateway-configuration.not-found"),
        general("resource.not-found"),
        invoice("resource.invoice.not-found"),
        payout("resource.payout.not-found"),
        permissionGroup("resource.permission-group.not-found"),
        plan("resource.plan.not-found"),
        product("resource.product.not-found"),
        project("resource.project.not-found"),
        refund("resource.refund.not-found"),
        route("request.route-not-found"),
        subscription("resource.subscription.not-found"),
        token("resource.token.not-found"),
        tokenizationRequest("resource.tokenization-request.not-found"),
        transaction("resource.transaction.not-found"),
        user("resource.user.not-found"),
        webhookEndpoint("resource.webhook-endpoint.not-found")
    }

    /**
     * Generic error that can’t be classified as specific.
     */
    @Parcelize
    @Suppress("EnumEntryName")
    enum class GenericCode(val rawValue: String) : Parcelable {
        mobile("processout-mobile.generic.error"),
        mobileAppProcessKilled("processout-mobile.generic.app-process-killed"),
        cardExceededLimits("card.exceeded-limits"),
        cardFailedCvc("card.failed-cvc"),
        cardInvalidCvc("card.invalid-cvc"),
        cardIssuerDown("card.issuer-down"),
        cardIssuerFailed("card.issuer-failed"),
        cardNoMoney("card.no-money"),
        cardNotAuthorized("card.not-authorized"),
        gatewayDeclined("gateway.declined"),
        gatewayUnknownError("gateway.unknown-error"),
        requestBadFormat("request.bad-format"),
        requestCardAlreadyUsed("request.source.card-already-used"),
        requestInvalidCard("request.card.invalid"),
        requestInvalidExpand("request.expand.invalid"),
        requestInvalidFilter("request.filter.invalid"),
        requestStillProcessing("request.still-processing"),
        requestTooMuch("request.too-much"),
        paymentDeclined("payment.declined"),
        cardNeedsAuthentication("card.needs-authentication"),
        cardDeclined("card.declined"),
        cardDoNotHonor("card.do-not-honor"),
        cardNoActionTaken("card.no-action-taken"),
        cardPleaseRetry("card.please-retry"),
        cardSecurityViolation("card.security-violation"),
        cardAcquirerFailed("card.acquirer-failed"),
        cardProcessingError("card.processing-error"),
        cardMaximumAttempts("card.maximum-attempts"),
        cardContactBank("card.contact-bank"),
        cardExceededWithdrawalLimit("card.exceeded-withdrawal-limit"),
        cardExceededActivityLimits("card.exceeded-activity-limits"),
        cardDuplicate("card.duplicate"),
        cardIssuerNotFound("card.issuer-not-found"),
        cardNetworkFailed("card.network-failed"),
        cardNotSupported("card.not-supported"),
        cardCurrencyUnsupported("card.currency-unsupported"),
        cardTypeNotSupported("card.type-not-supported"),
        cardNotActivated("card.not-activated"),
        cardExpired("card.expired"),
        cardInvalid("card.invalid"),
        cardInvalidNumber("card.invalid-number"),
        cardInvalidPin("card.invalid-pin"),
        cardInvalidName("card.invalid-name"),
        cardInvalidExpiryDate("card.invalid-expiry-date"),
        cardInvalidExpiryMonth("card.invalid-expiry-month"),
        cardInvalidExpiryYear("card.invalid-expiry-year"),
        cardInvalidZip("card.invalid-zip"),
        cardInvalidAddress("card.invalid-address"),
        cardMissingCvc("card.missing-cvc"),
        cardMissingExpiry("card.missing-expiry"),
        cardMissingNumber("card.missing-number"),
        cardMissing3DS("card.missing-3ds"),
        cardFailedAvs("card.failed-avs"),
        cardFailedAvsPostal("card.failed-avs-postal"),
        cardUnsupported3DS("card.unsupported-3ds"),
        cardFailed3DS("card.failed-3ds"),
        cardExpired3DS("card.expired-3ds"),
        cardFailedAvsAddress("card.failed-avs-address"),
        cardFailedCvcAndAvs("card.failed-cvc-and-avs"),
        cardBadTrackData("card.bad-track-data"),
        cardNotRegistered("card.not-registered"),
        cardStolen("card.stolen"),
        cardLost("card.lost"),
        cardDontRetry("card.dont-retry"),
        cardInvalidAccount("card.invalid-account"),
        cardRevoked("card.revoked"),
        cardRevokedAll("card.revoked-all"),
        cardTest("card.test"),
        cardBlacklisted("card.blacklisted"),
        requestTransactionBlocked("request.transaction-blocked"),
        requestGatewayNotAvailable("request.gateway.not-available"),
        requestGatewayOperationNotSupported("request.gateway.operation-not-supported"),
        requestInvalidIdempotency("request.idempotency-key.invalid"),
        requestInvalidPagination("request.pagination.invalid"),
        requestInvalidSource("request.source.invalid"),
        requestNoGatewayConfiguration("request.configuration.missing-gateway-configuration"),
        requestRateExceeded("request.rate.exceeded"),
        resourceNotLinked("resource.not-linked"),
        routingRulesTransactionBlocked("routing-rules.transaction-blocked"),
        sandboxNotSupported("sandbox.not-supported"),
        serviceNotSupported("service.not-supported")
    }

    /**
     * Operation is timed out.
     */
    @Parcelize
    @Suppress("EnumEntryName")
    enum class TimeoutCode(val rawValue: String) : Parcelable {
        mobile("processout-mobile.timeout"),
        gateway("gateway.timeout")
    }

    /**
     * Something went wrong on the ProcessOut side. This is extremely rare.
     */
    @Parcelize
    @Suppress("EnumEntryName")
    enum class InternalCode(val rawValue: String) : Parcelable {
        mobile("processout-mobile.internal"),
        gateway("gateway-internal-error")
    }
}

/**
 * Raw error code. Consistent with iOS SDK.
 */
val POFailure.Code.rawValue: String
    get() = when (this) {
        is POFailure.Code.Authentication -> authenticationCode.rawValue
        is POFailure.Code.Validation -> validationCode.rawValue
        is POFailure.Code.NotFound -> notFoundCode.rawValue
        is POFailure.Code.Generic -> genericCode.rawValue
        POFailure.Code.Cancelled -> "processout-mobile.cancelled"
        POFailure.Code.NetworkUnreachable -> "processout-mobile.network-unreachable"
        is POFailure.Code.Timeout -> timeoutCode.rawValue
        is POFailure.Code.Internal -> internalCode.rawValue
        is POFailure.Code.Unknown -> rawValue
    }
