package com.processout.sdk.ui.checkout

import android.os.Parcelable
import androidx.annotation.ColorRes
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.style.POActionsContainerStyle
import com.processout.sdk.ui.core.style.POBorderStyle
import com.processout.sdk.ui.core.style.POFieldStyle
import com.processout.sdk.ui.core.style.POTextStyle
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
        val regularPayment: RegularPaymentStyle? = null,
        val field: POFieldStyle? = null,
        val actionsContainer: POActionsContainerStyle? = null,
        @ColorRes
        val backgroundColorResId: Int? = null
    ) : Parcelable

    @Parcelize
    data class RegularPaymentStyle(
        val title: POTextStyle,
        val description: POTextStyle,
        val border: POBorderStyle
    ) : Parcelable
}
