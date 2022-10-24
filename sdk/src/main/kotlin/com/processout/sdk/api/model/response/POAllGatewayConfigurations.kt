package com.processout.sdk.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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
    val createdAt: String, //TODO: date adapter
    @Json(name = "enabled_at")
    val enabledAt: String? //TODO: date adapter
)

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
    @Json(name = "native_apm_config")
    val nativeApmConfig: PONativeAlternativePaymentMethodConfig?
)

@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentMethodConfig(
    val parameters: List<PONativeAlternativePaymentMethodParameter>?
)

@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentMethodParameter(
    val key: String,
    val length: Int?,
    val required: Boolean,
    val type: String //TODO: enum adapter [numeric, text, email, phone]
)
