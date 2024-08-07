package com.processout.sdk.ui.checkout

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.style.*
import com.processout.sdk.ui.shared.configuration.POActionConfirmationConfiguration
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
        val cancelButton: CancelButton? = CancelButton()
    ) : Parcelable

    @Parcelize
    data class CancelButton(
        val text: String? = null,
        val confirmation: POActionConfirmationConfiguration? = null
    ) : Parcelable

    @Parcelize
    data class Style(
        val regularPayment: RegularPaymentStyle? = null,
        val label: POTextStyle? = null,
        val field: POFieldStyle? = null,
        val codeField: POFieldStyle? = null,
        val radioButton: PORadioButtonStyle? = null,
        val dropdownMenu: PODropdownMenuStyle? = null,
        val bodyText: POTextStyle? = null,
        val errorText: POTextStyle? = null,
        val actionsContainer: POActionsContainerStyle? = null,
        val dialog: PODialogStyle? = null,
        @ColorRes
        val backgroundColorResId: Int? = null,
        @ColorRes
        val progressIndicatorColorResId: Int? = null,
        @ColorRes
        val controlsTintColorResId: Int? = null
    ) : Parcelable

    @Parcelize
    data class RegularPaymentStyle(
        val title: POTextStyle,
        val border: POBorderStyle,
        val description: POTextStyle,
        @DrawableRes
        val descriptionIconResId: Int? = null
    ) : Parcelable
}
