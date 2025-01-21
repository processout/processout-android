package com.processout.sdk.ui.savedpaymentmethods

import android.os.Parcelable
import androidx.annotation.ColorRes
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
    val title: String? = null,
    val cancellation: POCancellationConfiguration = POCancellationConfiguration(),
    val style: Style? = null
) : Parcelable {

    @Parcelize
    data class Style(
        val headerStyle: HeaderStyle? = null,
        @ColorRes
        val backgroundColorResId: Int? = null
    ) : Parcelable

    @Parcelize
    data class HeaderStyle(
        val title: POTextStyle,
        @ColorRes
        val dividerColorResId: Int? = null,
        @ColorRes
        val dragHandleColorResId: Int? = null,
        @ColorRes
        val backgroundColorResId: Int? = null
    ) : Parcelable
}
