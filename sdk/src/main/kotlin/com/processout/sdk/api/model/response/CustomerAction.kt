package com.processout.sdk.api.model.response

import com.processout.sdk.core.util.findBy
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class CustomerAction(
    @Json(name = "type")
    val rawType: String,
    val value: String
) {
    fun type() = Type::rawType.findBy(rawType) ?: Type.UNSUPPORTED

    @JsonClass(generateAdapter = false)
    enum class Type(val rawType: String) {
        FINGERPRINT_MOBILE("fingerprint-mobile"),
        CHALLENGE_MOBILE("challenge-mobile"),
        FINGERPRINT("fingerprint"),
        REDIRECT("redirect"),
        URL("url"),
        UNSUPPORTED(String())
    }
}
