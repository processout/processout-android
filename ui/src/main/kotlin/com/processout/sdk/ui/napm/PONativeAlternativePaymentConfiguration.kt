package com.processout.sdk.ui.napm

import android.os.Parcelable
import androidx.annotation.ColorRes
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.style.POActionsContainerStyle
import com.processout.sdk.ui.core.style.PODropdownMenuStyle
import com.processout.sdk.ui.core.style.POFieldStyle
import com.processout.sdk.ui.core.style.POTextStyle
import kotlinx.parcelize.Parcelize

/**
 * Defines native alternative payment configuration.
 *
 * @param[invoiceId] Invoice ID.
 * @param[gatewayConfigurationId] Gateway configuration ID.
 * @param[options] Allows to customize behaviour and pre-define the values.
 * @param[style] Allows to customize the look and feel.
 */
/** @suppress */
@ProcessOutInternalApi
@Parcelize
data class PONativeAlternativePaymentConfiguration(
    val invoiceId: String,
    val gatewayConfigurationId: String,
    val options: Options = Options(),
    val style: Style? = null
) : Parcelable {

    /**
     * Allows to customize behaviour and pre-define the values.
     *
     * @param[title] Custom title.
     * @param[primaryActionText] Custom primary action text (e.g. "Pay").
     * @param[secondaryAction] Secondary action (e.g. "Cancel"). Use _null_ to hide, this is a default behaviour.
     * @param[cancellation] Specifies cancellation behaviour.
     * @param[paymentConfirmation] Specifies payment confirmation behaviour.
     * @param[inlineSingleSelectValuesLimit] Defines maximum number of options that will be
     * displayed inline for parameters where user should select single option (e.g. radio buttons).
     * Default value is _5_.
     * @param[skipSuccessScreen] Only applies when [PaymentConfirmationConfiguration.waitsConfirmation] is _true_.
     * @param[successMessage] Custom success message when payment is completed.
     */
    @Parcelize
    data class Options(
        val title: String? = null,
        val primaryActionText: String? = null,
        val secondaryAction: SecondaryAction? = null,
        val cancellation: CancellationConfiguration = CancellationConfiguration(),
        val paymentConfirmation: PaymentConfirmationConfiguration = PaymentConfirmationConfiguration(),
        val inlineSingleSelectValuesLimit: Int = 5,
        val skipSuccessScreen: Boolean = false,
        val successMessage: String? = null
    ) : Parcelable

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
     * @param[backPressed] Cancel on back button press or back gesture. Default value is _true_.
     * @param[dragDown] Cancel when bottom sheet is dragged down out of the screen. Default value is _true_.
     * @param[touchOutside] Cancel on touch of the outside dimmed area of the bottom sheet. Default value is _true_.
     */
    @Parcelize
    data class CancellationConfiguration(
        val backPressed: Boolean = true,
        val dragDown: Boolean = true,
        val touchOutside: Boolean = true
    ) : Parcelable

    /**
     * Specifies payment confirmation behaviour.
     *
     * @param[waitsConfirmation] Specifies whether flow should wait for payment confirmation from PSP
     * or will complete right after all user input is submitted. Default value is _true_.
     * @param[timeoutSeconds] Amount of time (in seconds) to wait for final payment confirmation.
     * Default value is 3 minutes, while maximum value is 15 minutes.
     * @param[showProgressIndicatorAfterSeconds] Show progress indicator during payment confirmation after provided delay (in seconds).
     * Use _null_ to hide, this is a default behaviour.
     * @param[secondaryAction] Secondary action (e.g. "Cancel") that could be optionally presented to user during payment confirmation stage.
     * Use _null_ to hide, this is a default behaviour.
     */
    @Parcelize
    data class PaymentConfirmationConfiguration(
        val waitsConfirmation: Boolean = true,
        val timeoutSeconds: Int = DEFAULT_TIMEOUT_SECONDS,
        val showProgressIndicatorAfterSeconds: Int? = null,
        val secondaryAction: SecondaryAction? = null
    ) : Parcelable {
        companion object {
            const val MAX_TIMEOUT_SECONDS = 15 * 60
            const val DEFAULT_TIMEOUT_SECONDS = 3 * 60
        }
    }

    @Parcelize
    data class Style(
        val title: POTextStyle? = null,
        val field: POFieldStyle? = null,
        val dropdownMenu: PODropdownMenuStyle? = null,
        val errorMessage: POTextStyle? = null,
        val actionsContainer: POActionsContainerStyle? = null,
        @ColorRes
        val backgroundColorResId: Int? = null,
        @ColorRes
        val dividerColorResId: Int? = null,
        @ColorRes
        val dragHandleColorResId: Int? = null
    ) : Parcelable
}
