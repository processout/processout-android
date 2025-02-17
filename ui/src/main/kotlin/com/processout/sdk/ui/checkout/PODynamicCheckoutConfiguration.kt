@file:Suppress("unused")

package com.processout.sdk.ui.checkout

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import com.google.android.gms.wallet.WalletConstants
import com.processout.sdk.api.model.request.POContact
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration.GooglePayConfiguration.Environment
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.shared.image.PODrawableImage
import com.processout.sdk.ui.core.style.*
import com.processout.sdk.ui.shared.configuration.POActionConfirmationConfiguration
import com.processout.sdk.ui.shared.configuration.POBarcodeConfiguration
import kotlinx.parcelize.Parcelize

/**
 * @param[invoiceRequest] Request to fetch invoice for payment.
 * @param[expressCheckout] Express checkout section configuration.
 * @param[card] Card payment configuration.
 * @param[googlePay] Google Pay configuration.
 * @param[alternativePayment] Alternative payment configuration.
 * @param[submitButton] Submit button configuration.
 * @param[cancelButton] Cancel button configuration.
 * @param[cancelOnBackPressed] Specifies whether the screen should be cancelled on back button press or back gesture.
 * Default value is _true_.
 * @param[preselectSinglePaymentMethod] Specifies whether the single non-express payment method should be preselected automatically.
 * Default value is _true_.
 * @param[paymentSuccess] Payment success screen configuration. Pass _null_ to skip the success screen.
 * @param[style] Custom style.
 */
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

    /**
     * Specifies express checkout section configuration.
     *
     * @param[title] Custom section title.
     * @param[settingsButton] Settings button configuration.
     */
    @Parcelize
    data class ExpressCheckout(
        val title: String? = null,
        val settingsButton: Button? = null
    ) : Parcelable

    /**
     * Specifies card payment configuration.
     *
     * @param[billingAddress] Specifies billing address configuration.
     * @param[metadata] Metadata related to the card.
     */
    @Parcelize
    data class CardConfiguration(
        val billingAddress: BillingAddressConfiguration = BillingAddressConfiguration(),
        val metadata: Map<String, String>? = null
    ) : Parcelable {

        /**
         * Specifies billing address configuration.
         *
         * @param[defaultAddress] Default address information.
         * @param[attachDefaultsToPaymentMethod] Specifies whether the [defaultAddress] values should be attached to the card,
         * including fields that aren't displayed in the form.
         */
        @Parcelize
        data class BillingAddressConfiguration(
            val defaultAddress: POContact? = null,
            val attachDefaultsToPaymentMethod: Boolean = false
        ) : Parcelable
    }

    /**
     * Specifies Google Pay configuration.
     *
     * @param[environment] Google Pay environment.
     * @param[merchantName] Merchant name encoded as UTF-8. Merchant name is rendered in the payment sheet.
     * In [Environment.TEST], or if a merchant isn't recognized, a "Pay Unverified Merchant" message is displayed in the payment sheet.
     * @param[totalPriceLabel] Custom label for the total price within the display items.
     * @param[totalPriceStatus] The status of the total price used.
     * @param[checkoutOption] Affects the submit button text displayed in the Google Pay payment sheet.
     * @param[emailRequired] Set to _true_ to request an email address.
     * @param[billingAddress] Allows to set additional fields to be returned for a requested billing address.
     * @param[shippingAddress] Allows to set shipping restrictions.
     */
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

        /**
         * Google Pay environment.
         */
        @Parcelize
        enum class Environment(val value: Int) : Parcelable {
            /**
             * Corresponds to
             * [WalletConstants.ENVIRONMENT_TEST](https://developers.google.com/android/reference/com/google/android/gms/wallet/WalletConstants#ENVIRONMENT_TEST).
             */
            TEST(WalletConstants.ENVIRONMENT_TEST),

            /**
             * Corresponds to
             * [WalletConstants.ENVIRONMENT_PRODUCTION](https://developers.google.com/android/reference/com/google/android/gms/wallet/WalletConstants#ENVIRONMENT_PRODUCTION).
             */
            PRODUCTION(WalletConstants.ENVIRONMENT_PRODUCTION)
        }

        /**
         * The status of the total price used.
         */
        @Parcelize
        enum class TotalPriceStatus : Parcelable {
            /** Total price doesn't change from the amount presented to the shopper. */
            FINAL,

            /** Total price might adjust based on the details of the response, such as sales tax collected that's based on a billing address. */
            ESTIMATED
        }

        /**
         * Affects the submit button text displayed in the Google Pay payment sheet.
         */
        @Parcelize
        enum class CheckoutOption : Parcelable {
            /** Standard text applies for the given [totalPriceStatus] (default). */
            DEFAULT,

            /**
             * The selected payment method is charged immediately after the payer confirms their selections.
             * This option is only available when [totalPriceStatus] is set to [TotalPriceStatus.FINAL].
             */
            COMPLETE_IMMEDIATE_PURCHASE
        }

        /**
         * Allows to set additional fields to be returned for a requested billing address.
         *
         * @param[format] Billing address format required to complete the transaction.
         * @param[phoneNumberRequired] Set to _true_ if a phone number is required to process the transaction.
         */
        @Parcelize
        data class BillingAddressConfiguration(
            val format: Format,
            val phoneNumberRequired: Boolean
        ) : Parcelable {

            /**
             * Billing address format required to complete the transaction.
             */
            @Parcelize
            enum class Format : Parcelable {
                /** Name, country code, and postal code (default). */
                MIN,

                /** Name, street address, locality, region, country code, and postal code. */
                FULL
            }
        }

        /**
         * Allows to set shipping restrictions.
         *
         * @param[allowedCountryCodes] ISO 3166-1 alpha-2 country code values of the countries where shipping is allowed.
         * If this object isn't specified, all shipping address countries are allowed.
         * @param[phoneNumberRequired] Set to _true_ if a phone number is required for the provided shipping address.
         */
        @Parcelize
        data class ShippingAddressConfiguration(
            val allowedCountryCodes: Set<String>,
            val phoneNumberRequired: Boolean
        ) : Parcelable
    }

    /**
     * Specifies alternative payment configuration.
     *
     * @param[returnUrl] Deep link return URL for web authorization.
     * @param[inlineSingleSelectValuesLimit] Defines maximum number of options that will be
     * displayed inline for parameters where user should select single option (e.g. radio buttons).
     * Default value is _5_.
     * @param[barcode] Specifies barcode configuration.
     * @param[paymentConfirmation] Specifies payment confirmation configuration.
     */
    @Parcelize
    data class AlternativePaymentConfiguration(
        val returnUrl: String? = null,
        val inlineSingleSelectValuesLimit: Int = 5,
        val barcode: POBarcodeConfiguration = POBarcodeConfiguration(saveButton = POBarcodeConfiguration.Button()),
        val paymentConfirmation: PaymentConfirmationConfiguration = PaymentConfirmationConfiguration()
    ) : Parcelable {

        /**
         * Specifies payment confirmation configuration.
         *
         * @param[timeoutSeconds] Amount of time (in seconds) to wait for final payment confirmation.
         * Default value is 3 minutes, while maximum value is 15 minutes.
         * @param[showProgressIndicatorAfterSeconds] Show progress indicator during payment confirmation after provided delay (in seconds).
         * Use _null_ to hide, this is a default behaviour.
         * @param[confirmButton] Confirm button configuration.
         * @param[cancelButton] Cancel button configuration.
         */
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
     * Specifies payment success screen configuration.
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
