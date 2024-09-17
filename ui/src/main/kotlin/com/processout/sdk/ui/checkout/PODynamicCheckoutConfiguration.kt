package com.processout.sdk.ui.checkout

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.google.android.gms.wallet.WalletConstants
import com.processout.sdk.api.model.request.POContact
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
    val card: CardConfiguration = CardConfiguration(),
    val googlePay: GooglePayConfiguration = GooglePayConfiguration(),
    val alternativePayment: AlternativePaymentConfiguration = AlternativePaymentConfiguration(),
    val submitButtonText: String? = null,
    val cancelButton: CancelButton? = CancelButton(),
    val style: Style? = null
) : Parcelable {

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
        val environment: Environment = Environment.TEST
    ) : Parcelable {

        @Parcelize
        enum class Environment(val value: Int) : Parcelable {
            TEST(WalletConstants.ENVIRONMENT_TEST),
            PRODUCTION(WalletConstants.ENVIRONMENT_PRODUCTION)
        }
    }

    @Parcelize
    data class AlternativePaymentConfiguration(
        val returnUrl: String? = null,
        val inlineSingleSelectValuesLimit: Int = 5,
        val paymentConfirmation: PaymentConfirmationConfiguration = PaymentConfirmationConfiguration(),
    ) : Parcelable {

        @Parcelize
        data class PaymentConfirmationConfiguration(
            val timeoutSeconds: Int = 3 * 60,
            val showProgressIndicatorAfterSeconds: Int? = null,
            val cancelButton: CancelButton? = CancelButton()
        ) : Parcelable
    }

    @Parcelize
    data class CancelButton(
        val text: String? = null,
        val disabledForSeconds: Int = 0,
        val confirmation: POActionConfirmationConfiguration? = null
    ) : Parcelable

    @Parcelize
    data class Style(
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
