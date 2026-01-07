package com.processout.sdk.api.model.response.napm.v2

import android.os.Parcelable
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentAuthorizationRequest
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentRedirectConfirmation
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentTokenizationRequest
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
 * @param[confirmationRequired] Indicates whether it is required to notify the backend if the redirection was successful
 * by sending [PONativeAlternativePaymentRedirectConfirmation]
 * in the [PONativeAlternativePaymentAuthorizationRequest.redirectConfirmation]
 * or the [PONativeAlternativePaymentTokenizationRequest.redirectConfirmation],
 * depending on the flow.
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentRedirect(
    val url: String,
    val hint: String,
    @Json(name = "type")
    val rawType: String,
    @Json(name = "confirmation_required")
    val confirmationRequired: Boolean? // TODO: make non-nullable
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
