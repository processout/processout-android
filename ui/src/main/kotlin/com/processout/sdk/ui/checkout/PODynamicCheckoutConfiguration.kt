package com.processout.sdk.ui.checkout

import android.os.Parcelable
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.style.POFieldStyle
import kotlinx.parcelize.Parcelize

/** @suppress */
@ProcessOutInternalApi
@Parcelize
data class PODynamicCheckoutConfiguration(
    val invoiceRequest: POInvoiceRequest,
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
