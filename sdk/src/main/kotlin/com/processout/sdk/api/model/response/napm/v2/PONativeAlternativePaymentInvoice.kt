package com.processout.sdk.api.model.response.napm.v2

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.JsonClass

/**
 * Invoice details.
 *
 * @param[id] Invoice identifier.
 * @param[amount] Invoice amount.
 * @param[currency] Invoice currency.
 */
@ProcessOutInternalApi
@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentInvoice(
    val id: String,
    val amount: String,
    val currency: String
)
