package com.processout.sdk.api.service.googlepay

import com.processout.sdk.api.service.googlepay.POGooglePayConfiguration.CardConfiguration
import com.processout.sdk.api.service.googlepay.POGooglePayConfiguration.PaymentDataConfiguration
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import org.json.JSONArray
import org.json.JSONObject

@ProcessOutInternalApi
object POGooglePayRequestBuilder {

    fun isReadyToPayRequest(configuration: CardConfiguration): JSONObject =
        baseRequest.put("allowedPaymentMethods", allowedPaymentMethods(configuration))

    fun allowedPaymentMethods(configuration: CardConfiguration): JSONArray =
        JSONArray().put(cardPaymentMethod(configuration))

    fun paymentDataRequest(configuration: POGooglePayConfiguration): JSONObject =
        with(configuration) {
            val shippingAddressParameters = paymentData.shippingAddressParameters
            val request = baseRequest
                .put(
                    "allowedPaymentMethods", JSONArray().put(
                        cardPaymentMethod(card)
                            .put(
                                "tokenizationSpecification", tokenizationSpecification(
                                    gateway = gateway,
                                    gatewayMerchantId = gatewayMerchantId
                                )
                            )
                    )
                )
                .put("transactionInfo", transactionInfo(paymentData.transactionInfo))
                .put("emailRequired", paymentData.emailRequired)
                .put("shippingAddressRequired", paymentData.shippingAddressRequired)
            if (!paymentData.merchantName.isNullOrBlank()) {
                request.put(
                    "merchantInfo", JSONObject()
                        .put("merchantName", paymentData.merchantName)
                )
            }
            if (paymentData.shippingAddressRequired && shippingAddressParameters != null) {
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

    private fun cardPaymentMethod(configuration: CardConfiguration): JSONObject =
        with(configuration) {
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

    private fun transactionInfo(
        configuration: PaymentDataConfiguration.TransactionInfo
    ): JSONObject = with(configuration) {
        val request = JSONObject()
            .put("currencyCode", currencyCode)
            .put("transactionId", transactionId)
            .put("totalPrice", totalPrice)
            .put("totalPriceStatus", totalPriceStatus.toString())
            .put("checkoutOption", checkoutOption.toString())
        if (!countryCode.isNullOrBlank()) {
            request.put("countryCode", countryCode)
        }
        if (!totalPriceLabel.isNullOrBlank()) {
            request.put("totalPriceLabel", totalPriceLabel)
        }
        request
    }
}
