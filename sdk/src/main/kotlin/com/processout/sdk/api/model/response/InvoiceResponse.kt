package com.processout.sdk.api.model.response

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class InvoiceResponse(
    val invoice: POInvoice
)

/**
 * Invoice details.
 *
 * @param[id] Invoice identifier.
 * @param[amount] Invoice amount.
 * @param[currency] Invoice currency.
 * @param[returnUrl] Return URL or deep link for web based operations.
 * @param[paymentMethods] Dynamic checkout configuration.
 */
@JsonClass(generateAdapter = true)
data class POInvoice(
    val id: String,
    val amount: String,
    val currency: String,
    @Json(name = "return_url")
    val returnUrl: String?,
    @Json(name = "payment_methods")
    @ProcessOutInternalApi val paymentMethods: List<PODynamicCheckoutPaymentMethod>?
)

/**
 * Dynamic checkout configuration.
 */
sealed class PODynamicCheckoutPaymentMethod {

    //region Types

    /**
     * Dynamic checkout card configuration.
     *
     * @param[display] UI configuration.
     * @param[configuration] Card payment configuration.
     */
    @JsonClass(generateAdapter = true)
    data class Card(
        val display: Display,
        @Json(name = "card")
        val configuration: CardConfiguration
    ) : PODynamicCheckoutPaymentMethod()

    /**
     * Dynamic checkout Google Pay configuration.
     *
     * @param[configuration] Google Pay configuration.
     * @param[flow] Payment flow type.
     */
    @JsonClass(generateAdapter = true)
    data class GooglePay(
        @Json(name = "googlepay")
        val configuration: GooglePayConfiguration,
        val flow: Flow?
    ) : PODynamicCheckoutPaymentMethod()

    /**
     * Dynamic checkout alternative payment configuration.
     *
     * @param[display] UI configuration.
     * @param[configuration] Alternative payment configuration.
     * @param[flow] Payment flow type.
     */
    @JsonClass(generateAdapter = true)
    data class AlternativePayment(
        val display: Display,
        @Json(name = "apm")
        val configuration: AlternativePaymentConfiguration,
        val flow: Flow?
    ) : PODynamicCheckoutPaymentMethod()

    /**
     * Unknown dynamic checkout configuration.
     */
    data object Unknown : PODynamicCheckoutPaymentMethod()

    //endregion

    /**
     * UI configuration.
     *
     * @param[name] Payment method name.
     * @param[logo] Image resource for light/dark themes.
     * @param[brandColor] Brand color for light/dark themes.
     */
    @JsonClass(generateAdapter = true)
    data class Display(
        val name: String,
        val logo: POImageResource,
        @Json(name = "brand_color")
        val brandColor: BrandColor
    )

    /**
     * Brand color for light/dark themes.
     *
     * @param[light] Light color HEX.
     * @param[dark] Dark color HEX.
     */
    @JsonClass(generateAdapter = true)
    data class BrandColor(
        val light: String,
        val dark: String
    )

    /**
     * Card payment configuration.
     *
     * @param[requireCvc] Defines whether the card CVC should be collected.
     * @param[requireCardholderName] Defines whether the cardholder name should be collected.
     * @param[allowSchemeSelection] Defines whether the user will be asked to select the scheme if co-scheme is available.
     * @param[billingAddress] Card billing address configuration.
     */
    @JsonClass(generateAdapter = true)
    data class CardConfiguration(
        @Json(name = "require_cvc")
        val requireCvc: Boolean,
        @Json(name = "require_cardholder_name")
        val requireCardholderName: Boolean,
        @Json(name = "allow_scheme_selection")
        val allowSchemeSelection: Boolean,
        @Json(name = "billing_address")
        val billingAddress: BillingAddressConfiguration
    )

    /**
     * Billing address configuration.
     *
     * @param[collectionMode] Billing address collection mode.
     * @param[restrictToCountryCodes] Set of ISO country codes that is supported for the billing address.
     * When _null_, all countries are supported.
     */
    @JsonClass(generateAdapter = true)
    data class BillingAddressConfiguration(
        @Json(name = "collection_mode")
        val collectionMode: POBillingAddressCollectionMode,
        @Json(name = "restrict_to_country_codes")
        val restrictToCountryCodes: Set<String>?
    )

    /**
     * Google Pay configuration.
     * See [CardParameters](https://developers.google.com/pay/api/android/reference/request-objects#CardParameters).
     *
     * @param[allowedAuthMethods] Allowed card authentication methods.
     * @param[allowedCardNetworks] Allowed card networks.
     * @param[allowPrepaidCards] Set to _false_ if you don't support prepaid cards.
     * @param[allowCreditCards] Set to _false_ if you don't support credit cards. Required for UK Gambling merchants.
     */
    @JsonClass(generateAdapter = true)
    data class GooglePayConfiguration(
        @Json(name = "allowed_auth_methods")
        val allowedAuthMethods: Set<String>,
        @Json(name = "allowed_card_networks")
        val allowedCardNetworks: Set<String>,
        @Json(name = "allow_prepaid_cards")
        val allowPrepaidCards: Boolean,
        @Json(name = "allow_credit_cards")
        val allowCreditCards: Boolean
    )

    /**
     * Alternative payment configuration.
     *
     * @param[gatewayConfigurationId] Gateway configuration ID.
     * @param[gatewayName] Gateway name.
     * @param[redirectUrl] Redirect URL. If it's _null_, then payment should go through the native flow.
     */
    @JsonClass(generateAdapter = true)
    data class AlternativePaymentConfiguration(
        @Json(name = "gateway_configuration_uid")
        val gatewayConfigurationId: String,
        @Json(name = "gateway_name")
        val gatewayName: String,
        @Json(name = "redirect_url")
        val redirectUrl: String?
    )

    /**
     * Dynamic checkout payment flow types.
     */
    @Suppress("EnumEntryName")
    @JsonClass(generateAdapter = false)
    enum class Flow {
        express
    }
}
