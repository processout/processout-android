package com.processout.sdk.api.model.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class POCardUpdateCVCRequest(
    val cvc: String,
)
