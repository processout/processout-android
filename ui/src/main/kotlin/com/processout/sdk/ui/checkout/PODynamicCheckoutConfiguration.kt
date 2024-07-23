package com.processout.sdk.ui.checkout

import android.os.Parcelable
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.style.POFieldStyle
import kotlinx.parcelize.Parcelize

/**
 * @param[clientSecret] Client secret is a value of __x-processout-client-secret__ header of the invoice.
 * Customer's saved payment methods will be included when this value is provided.
 */
/** @suppress */
@ProcessOutInternalApi
@Parcelize
data class PODynamicCheckoutConfiguration(
    val invoiceId: String,
    val clientSecret: String? = null,
    val options: Options = Options(),
    val style: Style? = null
) : Parcelable {

    @Parcelize
    data class Options(
        val title: String? = null
    ) : Parcelable

    @Parcelize
    data class Style(
        val field: POFieldStyle? = null
    ) : Parcelable
}
