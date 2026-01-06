package com.processout.sdk.api.model.response.napm.v2

import android.os.Parcelable
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.processout.sdk.core.util.findBy
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * Specifies native alternative payment redirect parameters.
 *
 * @param[url] Redirect URL.
 * @param[hint] A hint or description associated with the redirect URL.
 * @param[rawType] Raw redirect type.
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentRedirect(
    val url: String,
    val hint: String,
    @Json(name = "type")
    val rawType: String
) : Parcelable {

    /** Redirect type. */
    val type: Type
        get() = Type::rawType.findBy(rawType) ?: Type.UNKNOWN

    /**
     * Redirect type.
     */
    enum class Type(val rawType: String) {
        /** Web redirect. */
        WEB("web"),

        /** Deep link redirect. */
        DEEP_LINK("deep_link"),

        /**
         * Placeholder that allows adding additional cases while staying backward compatible.
         * __Warning:__ Do not match this case directly, use _when-else_ instead.
         */
        @ProcessOutInternalApi
        UNKNOWN(String())
    }
}
