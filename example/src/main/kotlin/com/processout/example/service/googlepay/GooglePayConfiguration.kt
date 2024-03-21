/*
 * Copyright 2024 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.processout.example.service.googlepay

import android.content.Context
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Contains helper static methods for dealing with the Payments API.
 *
 * Many of the parameters used in the code are optional and are set here merely to call out their existence.
 * Please consult the documentation to learn more and feel free to remove ones not relevant to your implementation.
 */
object GooglePayConfiguration {

    val CENTS = BigDecimal(100)

    /**
     * Create a Google Pay API base request object with properties used in all requests.
     *
     * @return Google Pay API base request object.
     * @throws JSONException
     */
    private val baseRequest = JSONObject()
        .put("apiVersion", 2)
        .put("apiVersionMinor", 0)

    /**
     * Gateway Integration: Identify your gateway and your app's gateway merchant identifier.
     *
     * The Google Pay API response will return an encrypted payment method capable of being charged
     * by a supported gateway after payer authorization.
     * Check with your gateway on the parameters to pass and modify them in [GooglePayConstants].
     *
     * @return Payment data tokenization for the CARD payment method.
     * @throws JSONException
     * See [PaymentMethodTokenizationSpecification](https://developers.google.com/pay/api/android/reference/object.PaymentMethodTokenizationSpecification)
     */
    private val gatewayTokenizationSpecification: JSONObject =
        JSONObject()
            .put("type", "PAYMENT_GATEWAY")
            .put("parameters", JSONObject(GooglePayConstants.PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS))

    /**
     * Card networks supported by your app and your gateway.
     * Confirm card networks supported by your app and gateway & update in [GooglePayConstants].
     *
     * @return Allowed card networks
     * See [CardParameters](https://developers.google.com/pay/api/android/reference/object.CardParameters)
     */
    private val allowedCardNetworks = JSONArray(GooglePayConstants.SUPPORTED_NETWORKS)

    /**
     * Card authentication methods supported by your app and your gateway.
     * Confirm your processor supports Android device tokens
     * on your supported card networks and make updates in [GooglePayConstants].
     *
     * @return Allowed card authentication methods.
     * See [CardParameters](https://developers.google.com/pay/api/android/reference/object.CardParameters)
     */
    private val allowedCardAuthMethods = JSONArray(GooglePayConstants.SUPPORTED_METHODS)

    /**
     * Describe your app's support for the CARD payment method.
     * The provided properties are applicable to both an IsReadyToPayRequest and a PaymentDataRequest.
     *
     * @return A CARD PaymentMethod object describing accepted cards.
     * @throws JSONException
     * See [PaymentMethod](https://developers.google.com/pay/api/android/reference/object.PaymentMethod)
     */
    // Optionally, you can add billing address/phone number associated with a CARD payment method.
    private fun baseCardPaymentMethod(): JSONObject =
        JSONObject()
            .put("type", "CARD")
            .put(
                "parameters", JSONObject()
                    .put("allowedAuthMethods", allowedCardAuthMethods)
                    .put("allowedCardNetworks", allowedCardNetworks)
                    .put("billingAddressRequired", true)
                    .put(
                        "billingAddressParameters", JSONObject()
                            .put("format", "FULL")
                    )
            )

    /**
     * Describe the expected returned payment data for the CARD payment method.
     *
     * @return A CARD PaymentMethod describing accepted cards and optional fields.
     * @throws JSONException
     * See [PaymentMethod](https://developers.google.com/pay/api/android/reference/object.PaymentMethod)
     */
    private val cardPaymentMethod: JSONObject = baseCardPaymentMethod()
        .put("tokenizationSpecification", gatewayTokenizationSpecification)

    val allowedPaymentMethods: JSONArray = JSONArray().put(cardPaymentMethod)

    /**
     * An object describing accepted forms of payment by your app, used to determine a viewer's readiness to pay.
     *
     * @return API version and payment methods supported by the app.
     * See [IsReadyToPayRequest](https://developers.google.com/pay/api/android/reference/object.IsReadyToPayRequest)
     */
    fun isReadyToPayRequest(): JSONObject = baseRequest
        .put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod()))

    /**
     * Information about the merchant requesting payment information.
     *
     * @return Information about the merchant.
     * @throws JSONException
     * See [MerchantInfo](https://developers.google.com/pay/api/android/reference/object.MerchantInfo)
     */
    private val merchantInfo: JSONObject =
        JSONObject().put("merchantName", "Example Merchant")

    /**
     * Creates an instance of [PaymentsClient] for use in an [Context]
     * using the environment and theme set in [GooglePayConstants].
     *
     * @param context from the caller activity.
     */
    fun createPaymentsClient(context: Context): PaymentsClient {
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(GooglePayConstants.PAYMENTS_ENVIRONMENT)
            .build()

        return Wallet.getPaymentsClient(context, walletOptions)
    }

    /**
     * Provide Google Pay API with a payment amount, currency, and amount status.
     *
     * @return Information about the requested payment.
     * @throws JSONException
     * See [TransactionInfo](https://developers.google.com/pay/api/android/reference/object.TransactionInfo)
     */
    private fun getTransactionInfo(price: String): JSONObject =
        JSONObject()
            .put("totalPrice", price)
            .put("totalPriceStatus", "FINAL")
            .put("countryCode", GooglePayConstants.COUNTRY_CODE)
            .put("currencyCode", GooglePayConstants.CURRENCY_CODE)

    /**
     * An object describing information requested in a Google Pay payment sheet.
     *
     * @return Payment data expected by your app.
     * See [PaymentDataRequest](https://developers.google.com/pay/api/android/reference/object.PaymentDataRequest)
     */
    fun getPaymentDataRequest(priceCents: Long): JSONObject =
        baseRequest
            .put("allowedPaymentMethods", allowedPaymentMethods)
            .put("transactionInfo", getTransactionInfo(priceCents.centsToString()))
            .put("merchantInfo", merchantInfo)
            .put("shippingAddressRequired", true)
            .put(
                "shippingAddressParameters", JSONObject()
                    .put("phoneNumberRequired", false)
                    .put("allowedCountryCodes", JSONArray(listOf("US", "GB")))
            )
}

/**
 * Converts cents to a string format accepted by [GooglePayConfiguration.getPaymentDataRequest].
 */
fun Long.centsToString() = BigDecimal(this)
    .divide(GooglePayConfiguration.CENTS)
    .setScale(2, RoundingMode.HALF_EVEN)
    .toString()
