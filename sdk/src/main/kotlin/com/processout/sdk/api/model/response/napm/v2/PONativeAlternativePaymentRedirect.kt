package com.processout.sdk.api.model.response.napm.v2

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * Specifies native alternative payment redirect parameters.
 *
 * @param[url] Redirect URL.
 * @param[hint] A hint or description associated with the redirect URL.
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentRedirect(
    val url: String,
    val hint: String
) : Parcelable
