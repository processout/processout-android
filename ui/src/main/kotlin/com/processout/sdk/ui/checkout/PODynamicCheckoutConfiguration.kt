package com.processout.sdk.ui.checkout

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
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
            @IntRange(from = 0, to = 15 * 60)
            val timeoutSeconds: Int = 3 * 60,
            val showProgressIndicatorAfterSeconds: Int? = null,
            val confirmButton: Button? = null,
            val cancelButton: CancelButton? = CancelButton()
        ) : Parcelable

        /**
         * Cancel button configuration.
         *
         * @param[text] Button text. Pass _null_ to use default text.
         * @param[icon] Button icon drawable resource. Pass _null_ to hide.
         * @param[disabledForSeconds] Initially disables the button for the given amount of time in seconds.
         * By default user can interact with the button immediately when it's visible.
         * @param[confirmation] Specifies action confirmation configuration (e.g. dialog).
         * Use _null_ to disable, this is a default behaviour.
         */
        @Parcelize
        data class CancelButton(
            val text: String? = null,
            val icon: PODrawableImage? = null,
            val disabledForSeconds: Int = 0,
            val confirmation: POActionConfirmationConfiguration? = null
        ) : Parcelable
    }

    /**
     * Button configuration.
     *
     * @param[text] Button text. Pass _null_ to use default text.
     * @param[icon] Button icon drawable resource. Pass _null_ to hide.
     */
    @Parcelize
    data class Button(
        val text: String? = null,
        val icon: PODrawableImage? = null
    ) : Parcelable

    /**
     * Cancel button configuration.
     *
     * @param[text] Button text. Pass _null_ to use default text.
     * @param[icon] Button icon drawable resource. Pass _null_ to hide.
     * @param[confirmation] Specifies action confirmation configuration (e.g. dialog).
     * Use _null_ to disable, this is a default behaviour.
     */
    @Parcelize
    data class CancelButton(
        val text: String? = null,
        val icon: PODrawableImage? = null,
        val confirmation: POActionConfirmationConfiguration? = null
    ) : Parcelable

    /**
     * Payment success configuration.
     *
     * @param[message] Custom success message.
     * @param[durationSeconds] Defines for how long user will stay on success screen before calling completion.
     * Default value is 3 seconds.
     */
    @Parcelize
    data class PaymentSuccess(
        val message: String? = null,
        val durationSeconds: Int = 3
    ) : Parcelable

    /**
     * Specifies screen style.
     *
     * @param[sectionHeader] Section header style.
     * @param[googlePayButton] Google Pay button style.
     * @param[expressPaymentButton] Branded express payment button style.
     * @param[regularPayment] Regular payment style.
     * @param[label] Field label style.
     * @param[field] Field style.
     * @param[codeField] Code field style.
     * @param[radioButton] Radio button style.
     * @param[checkbox] Checkbox style.
     * @param[dropdownMenu] Dropdown menu style.
     * @param[bodyText] Body text style.
     * @param[errorText] Error text style.
     * @param[messageBox] Message box style.
     * @param[dialog] Dialog style.
     * @param[actionsContainer] Style of action buttons and their container.
     * @param[backgroundColorResId] Color resource ID for background.
     * @param[progressIndicatorColorResId] Color resource ID for progress indicator.
     * @param[controlsTintColorResId] Color resource ID for tint that applies to generic components (e.g. selectable text).
     * @param[paymentSuccess] Payment success style.
     */
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
        val dialog: PODialogStyle? = null,
        val actionsContainer: POActionsContainerStyle? = null,
        @ColorRes
        val backgroundColorResId: Int? = null,
        @ColorRes
        val progressIndicatorColorResId: Int? = null,
        @ColorRes
        val controlsTintColorResId: Int? = null,
        val paymentSuccess: PaymentSuccessStyle? = null
    ) : Parcelable

    /**
     * Specifies section header style.
     *
     * @param[title] Title style.
     * @param[trailingButton] Trailing button style.
     */
    @Parcelize
    data class SectionHeaderStyle(
        val title: POTextStyle,
        val trailingButton: POButtonStyle
    ) : Parcelable

    /**
     * Specifies regular payment style.
     *
     * @param[title] Title style.
     * @param[description] Description style.
     * @param[descriptionIconResId] Description icon drawable resource ID.
     * @param[border] Border style.
     * @param[backgroundColorResId] Color resource ID for background.
     */
    @Parcelize
    data class RegularPaymentStyle(
        val title: POTextStyle,
        val description: POTextStyle,
        @DrawableRes
        val descriptionIconResId: Int? = null,
        val border: POBorderStyle? = null,
        @ColorRes
        val backgroundColorResId: Int? = null
    ) : Parcelable

    /**
     * Specifies payment success style.
     *
     * @param[message] Message style.
     * @param[successImageResId] Success image drawable resource ID.
     * @param[backgroundColorResId] Color resource ID for background.
     */
    @Parcelize
    data class PaymentSuccessStyle(
        val message: POTextStyle,
        @DrawableRes
        val successImageResId: Int? = null,
        @ColorRes
        val backgroundColorResId: Int? = null
    ) : Parcelable
}
