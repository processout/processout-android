package com.processout.sdk.ui.checkout

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.google.android.gms.wallet.WalletConstants
import com.processout.sdk.api.model.request.POContact
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.shared.image.PODrawableImage
import com.processout.sdk.ui.core.style.*
import com.processout.sdk.ui.shared.configuration.POActionConfirmationConfiguration
import com.processout.sdk.ui.shared.configuration.POBarcodeConfiguration
import kotlinx.parcelize.Parcelize

/** @suppress */
@ProcessOutInternalApi
@Parcelize
data class PODynamicCheckoutConfiguration(
    val invoiceRequest: POInvoiceRequest,
    val expressCheckout: ExpressCheckout = ExpressCheckout(),
    val card: CardConfiguration = CardConfiguration(),
    val googlePay: GooglePayConfiguration = GooglePayConfiguration(),
    val alternativePayment: AlternativePaymentConfiguration = AlternativePaymentConfiguration(),
    val submitButton: Button = Button(),
    val cancelButton: CancelButton? = CancelButton(),
    val cancelOnBackPressed: Boolean = true,
    val preselectSinglePaymentMethod: Boolean = true,
    val paymentSuccess: PaymentSuccess? = PaymentSuccess(),
    val style: Style? = null
) : Parcelable {

    @Parcelize
    data class ExpressCheckout(
        val title: String? = null,
        val settingsButton: Button? = null
    ) : Parcelable

    @Parcelize
    data class CardConfiguration(
        val billingAddress: BillingAddressConfiguration = BillingAddressConfiguration(),
        val metadata: Map<String, String>? = null
    ) : Parcelable {

        @Parcelize
        data class BillingAddressConfiguration(
            val defaultAddress: POContact? = null,
            val attachDefaultsToPaymentMethod: Boolean = false
        ) : Parcelable
    }

    @Parcelize
    data class GooglePayConfiguration(
        val environment: Environment = Environment.TEST,
        val merchantName: String? = null,
        val totalPriceLabel: String? = null,
        val totalPriceStatus: TotalPriceStatus = TotalPriceStatus.FINAL,
        val checkoutOption: CheckoutOption = CheckoutOption.DEFAULT,
        val emailRequired: Boolean = false,
        val billingAddress: BillingAddressConfiguration? = null,
        val shippingAddress: ShippingAddressConfiguration? = null
    ) : Parcelable {

        @Parcelize
        enum class Environment(val value: Int) : Parcelable {
            TEST(WalletConstants.ENVIRONMENT_TEST),
            PRODUCTION(WalletConstants.ENVIRONMENT_PRODUCTION)
        }

        @Parcelize
        enum class TotalPriceStatus : Parcelable {
            FINAL, ESTIMATED
        }

        @Parcelize
        enum class CheckoutOption : Parcelable {
            DEFAULT, COMPLETE_IMMEDIATE_PURCHASE
        }

        @Parcelize
        data class BillingAddressConfiguration(
            val format: Format,
            val phoneNumberRequired: Boolean
        ) : Parcelable {

            @Parcelize
            enum class Format : Parcelable {
                MIN, FULL
            }
        }

        @Parcelize
        data class ShippingAddressConfiguration(
            val allowedCountryCodes: Set<String>,
            val phoneNumberRequired: Boolean
        ) : Parcelable
    }

    @Parcelize
    data class AlternativePaymentConfiguration(
        val returnUrl: String? = null,
        val inlineSingleSelectValuesLimit: Int = 5,
        val barcode: POBarcodeConfiguration = POBarcodeConfiguration(saveButton = POBarcodeConfiguration.Button()),
        val paymentConfirmation: PaymentConfirmationConfiguration = PaymentConfirmationConfiguration()
    ) : Parcelable {

        @Parcelize
        data class PaymentConfirmationConfiguration(
            val timeoutSeconds: Int = 3 * 60,
            val showProgressIndicatorAfterSeconds: Int? = null,
            val confirmButton: Button? = null,
            val cancelButton: CancelButton? = CancelButton()
        ) : Parcelable
    }

    @Parcelize
    data class Button(
        val text: String? = null,
        val icon: PODrawableImage? = null
    ) : Parcelable

    @Parcelize
    data class CancelButton(
        val text: String? = null,
        val icon: PODrawableImage? = null,
        val disabledForSeconds: Int = 0,
        val confirmation: POActionConfirmationConfiguration? = null
    ) : Parcelable

    @Parcelize
    data class PaymentSuccess(
        val message: String? = null,
        val durationSeconds: Int = 3
    ) : Parcelable

    @Parcelize
    data class Style(
        val sectionHeader: SectionHeaderStyle? = null,
        val googlePayButton: POGooglePayButtonStyle? = null,
        val expressPaymentButton: POBrandButtonStyle? = null,
        val regularPayment: RegularPaymentStyle? = null,
        val label: POTextStyle? = null,
        val field: POFieldStyle? = null,
        val codeField: POFieldStyle? = null,
        val radioButton: PORadioButtonStyle? = null,
        val checkbox: POCheckboxStyle? = null,
        val dropdownMenu: PODropdownMenuStyle? = null,
        val bodyText: POTextStyle? = null,
        val errorText: POTextStyle? = null,
        val messageBox: POMessageBoxStyle? = null,
        val actionsContainer: POActionsContainerStyle? = null,
        val dialog: PODialogStyle? = null,
        @ColorRes
        val backgroundColorResId: Int? = null,
        @ColorRes
        val progressIndicatorColorResId: Int? = null,
        @ColorRes
        val controlsTintColorResId: Int? = null,
        val paymentSuccess: PaymentSuccessStyle? = null
    ) : Parcelable

    @Parcelize
    data class SectionHeaderStyle(
        val title: POTextStyle,
        val trailingButton: POButtonStyle
    ) : Parcelable

    @Parcelize
    data class RegularPaymentStyle(
        val title: POTextStyle,
        val border: POBorderStyle,
        val description: POTextStyle,
        @DrawableRes
        val descriptionIconResId: Int? = null
    ) : Parcelable

    @Parcelize
    data class PaymentSuccessStyle(
        val message: POTextStyle,
        @DrawableRes
        val successImageResId: Int? = null,
        @ColorRes
        val backgroundColorResId: Int? = null
    ) : Parcelable
}
