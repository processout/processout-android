package com.processout.sdk.api.model.response

import com.squareup.moshi.JsonClass

/**
 * Billing address collection modes.
 */
@Suppress("EnumEntryName")
@JsonClass(generateAdapter = false)
enum class POBillingAddressCollectionMode {
    /** Collect the full billing address. */
    full,

    /** Only collect the fields that are required by the particular payment method. */
    automatic,

    /** Never collect the billing address. */
    never
}
