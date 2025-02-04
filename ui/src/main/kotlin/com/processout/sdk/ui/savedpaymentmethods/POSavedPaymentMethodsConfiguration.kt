package com.processout.sdk.ui.savedpaymentmethods

import android.os.Parcelable
import androidx.annotation.ColorRes
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.shared.image.PODrawableImage
import com.processout.sdk.ui.core.style.*
import com.processout.sdk.ui.shared.configuration.POActionConfirmationConfiguration
import com.processout.sdk.ui.shared.configuration.POBottomSheetConfiguration
import com.processout.sdk.ui.shared.configuration.POBottomSheetConfiguration.Height.WrapContent
import kotlinx.parcelize.Parcelize

/** @suppress */
@ProcessOutInternalApi
@Parcelize
data class POSavedPaymentMethodsConfiguration(
    val invoiceRequest: POInvoiceRequest,
    val title: String? = null,
    val deleteButton: Button = Button(),
    val cancelButton: Button? = Button(),
    val bottomSheet: POBottomSheetConfiguration = POBottomSheetConfiguration(
        height = WrapContent,
        expandable = false
    ),
    val style: Style? = null
) : Parcelable {

    @Parcelize
    data class Button(
        val text: String? = null,
        val icon: PODrawableImage? = null,
        val confirmation: POActionConfirmationConfiguration? = null
    ) : Parcelable

    @Parcelize
    data class Style(
        val header: HeaderStyle? = null,
        val paymentMethod: PaymentMethodStyle? = null,
        val messageBox: POMessageBoxStyle? = null,
        val dialog: PODialogStyle? = null,
        val cancelButton: POButtonStyle? = null,
        @ColorRes
        val progressIndicatorColorResId: Int? = null,
        @ColorRes
        val backgroundColorResId: Int? = null
    ) : Parcelable

    @Parcelize
    data class HeaderStyle(
        val title: POTextStyle,
        @ColorRes
        val dragHandleColorResId: Int? = null,
        @ColorRes
        val dividerColorResId: Int? = null,
        @ColorRes
        val backgroundColorResId: Int? = null
    ) : Parcelable

    @Parcelize
    data class PaymentMethodStyle(
        val description: POTextStyle,
        val deleteButton: POButtonStyle,
        val border: POBorderStyle? = null,
        @ColorRes
        val backgroundColorResId: Int? = null
    ) : Parcelable
}
