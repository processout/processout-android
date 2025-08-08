package com.processout.sdk.api.model.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Request to get single invoice details.
 *
 * @param[invoiceId] Requested invoice ID.
 * @param[clientSecret] Client secret is a value of __X-ProcessOut-Client-Secret__ header of the invoice.
 * When provided payment methods saved by the customer will be included in the response if the invoice has assigned customer ID.
 * @param[expand] Expanded properties.
 */
@Parcelize
data class POInvoiceRequest(
    val invoiceId: String,
    val clientSecret: String? = null,
    val expand: Set<ExpandedProperty> = emptySet()
) : Parcelable {

    /**
     * Expandable invoice property.
     */
    @Parcelize
    data class ExpandedProperty internal constructor(
        internal val queryValue: String
    ) : Parcelable {

        companion object {
            /** Expands transaction. */
            val transaction = ExpandedProperty(queryValue = "transaction")

            /** Expands payment methods. */
            val paymentMethods = ExpandedProperty(queryValue = "payment_methods")
        }
    }
}
