package com.processout.sdk.api.model.request.napm.v2

import android.os.Parcelable
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentRequestConfiguration.ReturnRedirectType.AUTOMATIC
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * Payment configuration.
 *
 * @param[returnRedirectType] Return redirect type. By default [AUTOMATIC].
 */
@Parcelize
data class PONativeAlternativePaymentRequestConfiguration(
    val returnRedirectType: ReturnRedirectType = AUTOMATIC
) : Parcelable {

    /**
     * Return redirect type.
     */
    @JsonClass(generateAdapter = false)
    enum class ReturnRedirectType(val rawValue: String) {
        /** Redirect result is handled automatically. */
        AUTOMATIC("automatic"),

        /** Redirect result is not processed automatically and should be resolved explicitly. */
        MANUAL("manual")
    }
}
