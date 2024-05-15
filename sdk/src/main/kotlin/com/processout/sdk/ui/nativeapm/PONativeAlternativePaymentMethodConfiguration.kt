package com.processout.sdk.ui.nativeapm

import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.processout.sdk.ui.shared.style.POTextStyle
import com.processout.sdk.ui.shared.style.background.POBackgroundDecorationStyle
import com.processout.sdk.ui.shared.style.background.POBackgroundStyle
import com.processout.sdk.ui.shared.style.button.POButtonStyle
import com.processout.sdk.ui.shared.style.dialog.PODialogStyle
import com.processout.sdk.ui.shared.style.input.POInputFieldStyle
import com.processout.sdk.ui.shared.style.input.POInputStyle
import com.processout.sdk.ui.shared.style.radio.PORadioButtonStyle
import kotlinx.parcelize.Parcelize

/**
 * Defines native alternative payment method configuration.
 *
 * @param[gatewayConfigurationId] Gateway configuration ID.
 * @param[invoiceId] Invoice ID.
 * @param[options] Allows to customize behaviour and pre-define the values.
 * @param[style] Allows to customize the look and feel.
 */
@Parcelize
data class PONativeAlternativePaymentMethodConfiguration(
    val gatewayConfigurationId: String,
    val invoiceId: String,
    val options: Options = Options(),
    val style: Style? = null
) : Parcelable {

    /**
     * Allows to customize behaviour and pre-define the values.
     *
     * @param[title] Custom title.
     * @param[primaryActionText] Custom primary action text (e.g. "Pay").
     * @param[secondaryAction] Secondary action. To hide secondary action use _null_, this is a default behaviour.
     * @param[inlineSingleSelectValuesLimit] Defines maximum number of options that will be
     * displayed inline for parameters where user should select single option (e.g. radio buttons).
     * Default value is _5_.
     * @param[cancellation] Specifies cancellation behaviour.
     * @param[successMessage] Custom success message when payment is completed.
     * @param[skipSuccessScreen] Only applies when [waitsPaymentConfirmation] is _true_.
     * @param[waitsPaymentConfirmation] Specifies whether flow should wait for payment confirmation from PSP
     * or will complete right after all user’s input is submitted. Default value is _true_.
     * @param[paymentConfirmationTimeoutSeconds] Amount of time (in seconds) to wait for final payment confirmation.
     * Default value is 3 minutes, while maximum value is 15 minutes.
     * @param[paymentConfirmationSecondaryAction] Action that could be optionally presented to user during payment confirmation stage.
     * To hide action use _null_, this is a default behaviour.
     * @param[showPaymentConfirmationProgressIndicatorAfterSeconds] Show progress indicator during payment confirmation after provided delay (in seconds).
     * To hide progress indicator use _null_, this is a default behaviour.
     */
    @Parcelize
    data class Options(
        val title: String? = null,
        val primaryActionText: String? = null,
        val secondaryAction: SecondaryAction? = null,
        val inlineSingleSelectValuesLimit: Int = 5,
        val cancellation: Cancellation = Cancellation(),
        val successMessage: String? = null,
        val skipSuccessScreen: Boolean = false,
        val waitsPaymentConfirmation: Boolean = true,
        val paymentConfirmationTimeoutSeconds: Int = DEFAULT_PAYMENT_CONFIRMATION_TIMEOUT_SECONDS,
        val paymentConfirmationSecondaryAction: SecondaryAction? = null,
        val showPaymentConfirmationProgressIndicatorAfterSeconds: Int? = null
    ) : Parcelable {
        companion object {
            const val MAX_PAYMENT_CONFIRMATION_TIMEOUT_SECONDS = 15 * 60
            const val DEFAULT_PAYMENT_CONFIRMATION_TIMEOUT_SECONDS = 3 * 60
        }
    }

    /**
     * Supported secondary actions.
     */
    sealed class SecondaryAction : Parcelable {
        /**
         * Action for cancellation.
         *
         * @param[text] Action text. Pass _null_ to use default text.
         * @param[disabledForSeconds] Initially disables action for the given amount of time in seconds.
         * By default user can interact with action immediately when it's visible.
         */
        @Parcelize
        data class Cancel(
            val text: String? = null,
            val disabledForSeconds: Int = 0
        ) : SecondaryAction()
    }

    /**
     * Specifies cancellation behaviour.
     *
     * @param[dragDown] Cancel when bottom sheet is dragged down out of the screen. Default value is _true_.
     * @param[touchOutside] Cancel on touch of the outside dimmed area of the bottom sheet. Default value is _true_.
     * @param[backPressed] Cancel on back button press or back gesture. Default value is _true_.
     */
    @Parcelize
    data class Cancellation(
        val dragDown: Boolean = true,
        val touchOutside: Boolean = true,
        val backPressed: Boolean = true
    ) : Parcelable

    /**
     * Allows to customize the look and feel.
     *
     * @param[title] Title style.
     * @param[input] Input style.
     * @param[codeInput] Code input style.
     * @param[dropdownMenu] Dropdown menu style.
     * @param[radioButton] Radio button style.
     * @param[primaryButton] Primary button style.
     * @param[secondaryButton] Secondary button style.
     * @param[background] Background style.
     * @param[backgroundColor] __Deprecated.__ Background color.
     * @param[progressIndicatorColor] Color of progress indicator.
     * @param[controlsTintColor] Tint color that applies to generic components (e.g. selectable TextView).
     * @param[message] Message style.
     * @param[successMessage] Success message style.
     * @param[successImageResId] Success image drawable resource ID.
     * @param[backgroundDecoration] __Deprecated.__ Background decoration style.
     * @param[dialog] Dialog style.
     */
    @Parcelize
    data class Style(
        val title: POTextStyle? = null,
        val input: POInputStyle? = null,
        val codeInput: POInputStyle? = null,
        val dropdownMenu: POInputFieldStyle? = null,
        val radioButton: PORadioButtonStyle? = null,
        val primaryButton: POButtonStyle? = null,
        val secondaryButton: POButtonStyle? = null,
        val background: POBackgroundStyle? = null,
        @ColorInt
        @Deprecated("Use property 'background: POBackgroundStyle'.")
        val backgroundColor: Int? = null,
        @ColorInt
        val progressIndicatorColor: Int? = null,
        @ColorInt
        val controlsTintColor: Int? = null,
        val message: POTextStyle? = null,
        val successMessage: POTextStyle? = null,
        @DrawableRes
        val successImageResId: Int? = null,
        @Deprecated("Use property 'background: POBackgroundStyle'.")
        val backgroundDecoration: POBackgroundDecorationStyle? = null,
        val dialog: PODialogStyle? = null
    ) : Parcelable
}
