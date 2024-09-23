package com.processout.sdk.api.service.googlepay

import com.processout.sdk.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
data class POGooglePayConfiguration(
    val gateway: String,
    val gatewayMerchantId: String,
    val card: CardConfiguration,
    val paymentData: PaymentDataConfiguration
) {

    data class CardConfiguration(
        val allowedAuthMethods: Set<String>,
        val allowedCardNetworks: Set<String>,
        val allowPrepaidCards: Boolean,
        val allowCreditCards: Boolean,
        val assuranceDetailsRequired: Boolean,
        val billingAddressRequired: Boolean,
        val billingAddressParameters: BillingAddressParameters?
    ) {

        data class BillingAddressParameters(
            val format: Format,
            val phoneNumberRequired: Boolean
        ) {
            enum class Format {
                MIN, FULL
            }
        }
    }

    data class PaymentDataConfiguration(
        val transactionInfo: TransactionInfo,
        val merchantName: String?,
        val emailRequired: Boolean,
        val shippingAddressRequired: Boolean,
        val shippingAddressParameters: ShippingAddressParameters?
    ) {

        data class TransactionInfo(
            val currencyCode: String,
            val countryCode: String?,
            val transactionId: String,
            val totalPrice: String,
            val totalPriceLabel: String?,
            val totalPriceStatus: TotalPriceStatus,
            val checkoutOption: CheckoutOption
        ) {
            enum class TotalPriceStatus {
                FINAL, ESTIMATED
            }

            enum class CheckoutOption {
                DEFAULT, COMPLETE_IMMEDIATE_PURCHASE
            }
        }

        data class ShippingAddressParameters(
            val allowedCountryCodes: Set<String>,
            val phoneNumberRequired: Boolean
        )
    }
}
