package com.processout.sdk.api.service.googlepay

import com.processout.sdk.api.service.googlepay.POGooglePayConfiguration.PaymentDataParameters.TransactionInfo
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import org.json.JSONArray
import org.json.JSONObject

@ProcessOutInternalApi
object POGooglePayConfiguration {

    data class Parameters(
        val gateway: String,
        val gatewayMerchantId: String,
        val cardParameters: CardParameters,
        val paymentDataParameters: PaymentDataParameters
    )

    data class CardParameters(
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

    data class PaymentDataParameters(
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
                ESTIMATED, FINAL
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

    fun isReadyToPayRequest(parameters: CardParameters): JSONObject =
        baseRequest.put("allowedPaymentMethods", allowedPaymentMethods(parameters))

    fun allowedPaymentMethods(parameters: CardParameters): JSONArray =
        JSONArray().put(cardPaymentMethod(parameters))

    fun paymentDataRequest(parameters: Parameters): JSONObject =
        with(parameters) {
            val shippingAddressParameters = paymentDataParameters.shippingAddressParameters
            val request = baseRequest
                .put(
                    "allowedPaymentMethods", JSONArray().put(
                        cardPaymentMethod(cardParameters)
                            .put(
                                "tokenizationSpecification", tokenizationSpecification(
                                    gateway = gateway,
                                    gatewayMerchantId = gatewayMerchantId
                                )
                            )
                    )
                )
                .put("transactionInfo", transactionInfo(paymentDataParameters.transactionInfo))
                .put("emailRequired", paymentDataParameters.emailRequired)
                .put("shippingAddressRequired", paymentDataParameters.shippingAddressRequired)
            if (!paymentDataParameters.merchantName.isNullOrBlank()) {
                request.put(
                    "merchantInfo", JSONObject()
                        .put("merchantName", paymentDataParameters.merchantName)
                )
            }
            if (paymentDataParameters.shippingAddressRequired && shippingAddressParameters != null) {
                request.put(
                    "shippingAddressParameters", JSONObject()
                        .put("allowedCountryCodes", JSONArray(shippingAddressParameters.allowedCountryCodes))
                        .put("phoneNumberRequired", shippingAddressParameters.phoneNumberRequired)
                )
            }
            request
        }

    private val baseRequest = JSONObject()
        .put("apiVersion", 2)
        .put("apiVersionMinor", 0)

    private fun cardPaymentMethod(parameters: CardParameters): JSONObject =
        with(parameters) {
            val request = JSONObject()
                .put("type", "CARD")
                .put(
                    "parameters", JSONObject()
                        .put("allowedAuthMethods", JSONArray(allowedAuthMethods))
                        .put("allowedCardNetworks", JSONArray(allowedCardNetworks))
                        .put("allowPrepaidCards", allowPrepaidCards)
                        .put("allowCreditCards", allowCreditCards)
                        .put("assuranceDetailsRequired", assuranceDetailsRequired)
                        .put("billingAddressRequired", billingAddressRequired)
                )
            if (billingAddressRequired && billingAddressParameters != null) {
                request.put(
                    "billingAddressParameters", JSONObject()
                        .put("format", billingAddressParameters.format.toString())
                        .put("phoneNumberRequired", billingAddressParameters.phoneNumberRequired)
                )
            }
            request
        }

    private fun tokenizationSpecification(
        gateway: String,
        gatewayMerchantId: String
    ) = JSONObject()
        .put("type", "PAYMENT_GATEWAY")
        .put(
            "parameters", JSONObject(
                mapOf(
                    "gateway" to gateway,
                    "gatewayMerchantId" to gatewayMerchantId
                )
            )
        )

    private fun transactionInfo(parameters: TransactionInfo): JSONObject =
        with(parameters) {
            val transactionInfo = JSONObject()
                .put("currencyCode", currencyCode)
                .put("transactionId", transactionId)
                .put("totalPrice", totalPrice)
                .put("totalPriceStatus", totalPriceStatus.toString())
                .put("checkoutOption", checkoutOption.toString())
            if (!countryCode.isNullOrBlank()) {
                transactionInfo.put("countryCode", countryCode)
            }
            if (!totalPriceLabel.isNullOrBlank()) {
                transactionInfo.put("totalPriceLabel", totalPriceLabel)
            }
            transactionInfo
        }
}
