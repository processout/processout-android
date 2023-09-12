package com.processout.sdk.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

/**
 * Defines gateway configurations.
 *
 * @param[gatewayConfigurations] Available gateway configurations.
 * @param[totalCount] Total count of items.
 * @param[hasMore] Indicates whether there are more items to fetch.
 */
@JsonClass(generateAdapter = true)
data class POAllGatewayConfigurations(
    @Json(name = "gateway_configurations")
    val gatewayConfigurations: List<POGatewayConfiguration>,
    @Json(name = "total_count")
    val totalCount: Int,
    @Json(name = "has_more")
    val hasMore: Boolean
)

@JsonClass(generateAdapter = true)
internal data class POGatewayConfigurationResponse(
    @Json(name = "gateway_configuration")
    val gatewayConfiguration: POGatewayConfiguration
)

/**
 * Defines single gateway configuration.
 *
 * @param[id] Gateway configuration ID.
 * @param[gateway] Gateway that the configuration configures.
 * @param[gatewayId] ID of the gateway to which the gateway configuration belongs.
 * @param[gatewayName] Gateway name.
 * @param[name] Name of the gateway configuration.
 * @param[defaultCurrency] Default currency of the gateway configuration.
 * @param[merchantAccountCountryCode] Country code of merchant’s account.
 * @param[enabled] Indicates whether configuration is currently enabled or not.
 * @param[createdAt] Date at which the gateway configuration was created.
 * @param[enabledAt] Date at which the gateway configuration was enabled.
 * @param[subAccountsEnabled] Enabled sub accounts.
 */
@JsonClass(generateAdapter = true)
data class POGatewayConfiguration(
    val id: String,
    val gateway: POGateway?,
    @Json(name = "gateway_id")
    val gatewayId: Int,
    @Json(name = "gateway_name")
    val gatewayName: String?,
    val name: String?,
    @Json(name = "default_currency")
    val defaultCurrency: String,
    @Json(name = "merchant_account_country_code")
    val merchantAccountCountryCode: String?,
    val enabled: Boolean,
    @Json(name = "created_at")
    val createdAt: Date,
    @Json(name = "enabled_at")
    val enabledAt: Date?,
    @Json(name = "sub_accounts_enabled")
    val subAccountsEnabled: List<String>? = null
)

/**
 * Defines gateway.
 *
 * @param[name] Name of the payment gateway.
 * @param[displayName] Name of the payment gateway that can be displayed.
 * @param[logoUrl] Gateway’s logo URL.
 * @param[url] URL of the payment gateway.
 * @param[tags] Gateway tags. Mainly used to filter gateways depending on their attributes (e-wallets and such).
 * @param[canPullTransactions] Indicates whether the gateway can pull old transactions into ProcessOut.
 * @param[canRefund] Indicates whether gateway supports refunds.
 * @param[nativeApmConfig] __Deprecated.__ Native alternative payment method configuration.
 */
@JsonClass(generateAdapter = true)
data class POGateway(
    val name: String,
    @Json(name = "display_name")
    val displayName: String,
    @Json(name = "logo_url")
    val logoUrl: String,
    val url: String,
    val tags: List<String>,
    @Json(name = "can_pull_transactions")
    val canPullTransactions: Boolean,
    @Json(name = "can_refund")
    val canRefund: Boolean,
    @Deprecated("Use POInvoicesService.fetchNativeAlternativePaymentMethodTransactionDetails().")
    @Json(name = "native_apm_config")
    val nativeApmConfig: PONativeAlternativePaymentMethodConfig?
)

/**
 * Native alternative payment method configuration.
 *
 * @param[parameters] Field parameters.
 */
@Deprecated("Use POInvoicesService.fetchNativeAlternativePaymentMethodTransactionDetails().")
@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentMethodConfig(
    val parameters: List<PONativeAlternativePaymentMethodParameter>?
)
