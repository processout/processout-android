package com.processout.sdk.api.model.response

import androidx.annotation.RestrictTo
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class POInvoiceResponse(
    val invoice: POInvoice
)

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@JsonClass(generateAdapter = true)
data class POInvoice(
    val id: String
)
