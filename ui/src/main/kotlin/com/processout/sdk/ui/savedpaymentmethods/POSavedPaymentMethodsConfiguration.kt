package com.processout.sdk.ui.savedpaymentmethods

import android.os.Parcelable
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.style.POTextStyle
import com.processout.sdk.ui.shared.configuration.POCancellationConfiguration
import kotlinx.parcelize.Parcelize

/** @suppress */
@ProcessOutInternalApi
@Parcelize
data class POSavedPaymentMethodsConfiguration(
    val invoiceRequest: POInvoiceRequest,
    val cancellation: POCancellationConfiguration = POCancellationConfiguration(),
    val style: Style? = null
) : Parcelable {

    @Parcelize
    data class Style(
        val title: POTextStyle? = null
    ) : Parcelable
}
