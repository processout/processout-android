package com.processout.sdk.ui.savedpaymentmethods

import android.os.Parcelable
import androidx.annotation.ColorRes
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.ui.core.shared.image.PODrawableImage
import com.processout.sdk.ui.core.style.*
import com.processout.sdk.ui.shared.configuration.POActionConfirmationConfiguration
import com.processout.sdk.ui.shared.configuration.POBottomSheetConfiguration
import com.processout.sdk.ui.shared.configuration.POBottomSheetConfiguration.Height.WrapContent
import kotlinx.parcelize.Parcelize

/**
 * Specifies saved payment methods configuration.
 *
 * @param[invoiceRequest] Invoice request.
 * __Note:__ Make sure that [POInvoiceRequest.clientSecret] is set to include saved payment methods in the response.
 * @param[title] Custom title.
 * @param[deleteButton] Payment method's delete button configuration.
 * @param[cancelButton] Cancel button configuration. Use _null_ to hide.
 * @param[bottomSheet] Specifies bottom sheet configuration. By default will wrap content and is non-expandable.
 * @param[style] Custom screen style.
 */
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

    /**
     * Button configuration.
     *
     * @param[text] Button text. Pass _null_ to hide.
     * @param[icon] Button icon drawable resource. Pass _null_ to use the default icon.
     * @param[confirmation] Specifies action confirmation configuration (e.g. dialog).
     * Use _null_ to disable, this is a default behaviour.
     */
    @Parcelize
    data class Button(
        val text: String? = null,
        val icon: PODrawableImage? = null,
        val confirmation: POActionConfirmationConfiguration? = null
    ) : Parcelable

    /**
     * Specifies screen style.
     *
     * @param[header] Screen header style.
     * @param[paymentMethod] Payment method style.
     * @param[messageBox] Message box style.
     * @param[dialog] Dialog style.
     * @param[cancelButton] Cancel button style.
     * @param[progressIndicatorColorResId] Color resource ID for progress indicator.
     * @param[backgroundColorResId] Color resource ID for background.
     */
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

    /**
     * Specifies screen header style.
     *
     * @param[title] Title style.
     * @param[dragHandleColorResId] Color resource ID for drag handle.
     * @param[dividerColorResId] Color resource ID for divider.
     * @param[backgroundColorResId] Color resource ID for background.
     */
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

    /**
     * Specifies payment method style.
     *
     * @param[description] Description style.
     * @param[deleteButton] Delete button style.
     * @param[border] Border style.
     * @param[backgroundColorResId] Color resource ID for background.
     */
    @Parcelize
    data class PaymentMethodStyle(
        val description: POTextStyle,
        val deleteButton: POButtonStyle,
        val border: POBorderStyle? = null,
        @ColorRes
        val backgroundColorResId: Int? = null
    ) : Parcelable
}
