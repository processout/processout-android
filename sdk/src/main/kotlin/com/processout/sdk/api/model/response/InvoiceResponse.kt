package com.processout.sdk.api.model.response

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class InvoiceResponse(
    val invoice: InvoiceResponseBody
)

@JsonClass(generateAdapter = true)
internal data class InvoiceResponseBody(
    val id: String,
    val amount: String,
    val currency: String,
    @Json(name = "return_url")
    val returnUrl: String?,
    @Json(name = "customer_id")
    val customerId: String?,
    val transaction: POTransaction?,
    @Json(name = "payment_methods")
    val paymentMethods: List<PODynamicCheckoutPaymentMethod>?
)

/**
 * Invoice details.
 *
 * @param[id] Invoice identifier.
 * @param[amount] Invoice amount.
 * @param[currency] Invoice currency.
 * @param[returnUrl] Return URL or deep link for web based operations.
 * @param[customerId] Customer identifier.
 * @param[transaction] Transaction details.
 * @param[paymentMethods] Dynamic checkout configuration.
 * @param[clientSecret] Client secret is a value of __x-processout-client-secret__ header of the invoice.
 */
data class POInvoice(
    val id: String,
    val amount: String = String(),
    val currency: String = String(),
    val returnUrl: String? = null,
    val customerId: String? = null,
    @ProcessOutInternalApi val transaction: POTransaction? = null,
    @ProcessOutInternalApi val paymentMethods: List<PODynamicCheckoutPaymentMethod>? = null,
    @ProcessOutInternalApi val clientSecret: String? = null
)

/**
 * Dynamic checkout configuration.
 */
@ProcessOutInternalApi
sealed class PODynamicCheckoutPaymentMethod {

    //region Types

    /**
     * Dynamic checkout card payment configuration.
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
     * Dynamic checkout tokenized card payment configuration.
     * This payment method was previously saved by the customer.
     *
     * @param[display] UI configuration.
     * @param[configuration] Customer token payment configuration.
     * @param[flow] Payment flow type.
     */
    @JsonClass(generateAdapter = true)
    data class CardCustomerToken(
        val display: Display,
        @Json(name = "card_customer_token")
        val configuration: CustomerTokenConfiguration,
        val flow: Flow?
    ) : PODynamicCheckoutPaymentMethod()

    /**
     * Dynamic checkout Google Pay configuration.
     *
     * @param[configuration] Google Pay configuration.
     */
    @JsonClass(generateAdapter = true)
    data class GooglePay(
        @Json(name = "googlepay")
        val configuration: GooglePayConfiguration
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
     * Dynamic checkout tokenized alternative payment configuration.
     * This payment method was previously saved by the customer.
     *
     * @param[display] UI configuration.
     * @param[configuration] Customer token payment configuration.
     * @param[flow] Payment flow type.
     */
    @JsonClass(generateAdapter = true)
    data class AlternativePaymentCustomerToken(
        val display: Display,
        @Json(name = "apm_customer_token")
        val configuration: CustomerTokenConfiguration,
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
     * @param[description] Optional payment method description.
     */
    @JsonClass(generateAdapter = true)
    data class Display(
        val name: String,
        val logo: POImageResource,
        @Json(name = "brand_color")
        val brandColor: POColor,
        val description: String?
    )

    /**
     * Card payment configuration.
     *
     * @param[cvcRequired] Defines whether the card CVC should be collected.
     * @param[cardholderNameRequired] Defines whether the cardholder name should be collected.
     * @param[schemeSelectionAllowed] Defines whether the user will be asked to select the scheme if co-scheme is available.
     * @param[billingAddress] Card billing address configuration.
     * @param[savingAllowed] Defines whether saving of the payment method is allowed for future payments.
     */
    @JsonClass(generateAdapter = true)
    data class CardConfiguration(
        @Json(name = "cvc_required")
        val cvcRequired: Boolean,
        @Json(name = "cardholder_name_required")
        val cardholderNameRequired: Boolean,
        @Json(name = "scheme_selection_allowed")
        val schemeSelectionAllowed: Boolean,
        @Json(name = "billing_address")
        val billingAddress: BillingAddressConfiguration,
        @Json(name = "saving_allowed")
        val savingAllowed: Boolean
    )

    /**
     * Billing address configuration.
     *
     * @param[collectionMode] Billing address collection mode.
     * @param[restrictToCountryCodes] Set of ISO country codes that is supported for the billing address.
     * When _null_, all countries are provided.
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
     * @param[gateway] Gateway identifier.
     * @param[gatewayMerchantId] Gateway merchant ID.
     * @param[countryCode] The ISO 3166-1 alpha-2 country code where the transaction is processed.
     * This property is required for merchants who process transactions in European Economic Area (EEA) countries
     * and any other countries that are subject to Strong Customer Authentication (SCA).
     * Merchants must specify the acquirer bank country code.
     * @param[allowedAuthMethods] Allowed card authentication methods.
     * @param[allowedCardNetworks] Allowed card networks.
     * @param[allowPrepaidCards] Set to _false_ if you don't support prepaid cards.
     * @param[allowCreditCards] Set to _false_ if you don't support credit cards. Required for UK Gambling merchants.
     */
    @JsonClass(generateAdapter = true)
    data class GooglePayConfiguration(
        val gateway: String,
        @Json(name = "gateway_merchant_id")
        val gatewayMerchantId: String,
        @Json(name = "country_code")
        val countryCode: String,
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
     * @param[redirectUrl] Redirect URL. If it's _null_, then payment should go through the native flow.
     * @param[savingAllowed] Defines whether saving of the payment method is allowed for future payments.
     */
    @JsonClass(generateAdapter = true)
    data class AlternativePaymentConfiguration(
        @Json(name = "gateway_configuration_id")
        val gatewayConfigurationId: String,
        @Json(name = "redirect_url")
        val redirectUrl: String?,
        @Json(name = "saving_allowed")
        val savingAllowed: Boolean
    )

    /**
     * Customer token payment configuration.
     *
     * @param[customerTokenId] Customer token ID.
     * @param[redirectUrl] Redirect URL to payment method that was previously saved by the customer.
     * If it's _null_, then payment can be authorized with [customerTokenId].
     */
    @JsonClass(generateAdapter = true)
    data class CustomerTokenConfiguration(
        @Json(name = "customer_token_id")
        val customerTokenId: String,
        @Json(name = "redirect_url")
        val redirectUrl: String?,
        @Json(name = "removing_allowed", ignore = true) //TODO: remove ignore when ready
        val removingAllowed: Boolean = true //TODO: remove default value when ready
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
