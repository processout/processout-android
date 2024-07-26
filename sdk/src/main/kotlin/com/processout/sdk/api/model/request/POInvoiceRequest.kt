package com.processout.sdk.api.model.request

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * Request to get single invoice details.
 *
 * @param[invoiceId] Requested invoice ID.
 * @param[clientSecret] Client secret is a value of __x-processout-client-secret__ header of the invoice.
 * When provided payment methods saved by the customer will be included in the response if the invoice has assigned customer ID.
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class POInvoiceRequest(
    val invoiceId: String,
    val clientSecret: String? = null
) : Parcelable
