package com.processout.sdk.ui.nativeapm

import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.processout.sdk.ui.shared.style.POBackgroundStyle
import com.processout.sdk.ui.shared.style.POTextStyle
import com.processout.sdk.ui.shared.style.background.POBackgroundDecorationStyle
import com.processout.sdk.ui.shared.style.button.POButtonStyle
import com.processout.sdk.ui.shared.style.input.POInputFieldStyle
import com.processout.sdk.ui.shared.style.input.POInputStyle
import com.processout.sdk.ui.shared.style.radio.PORadioButtonStyle
import kotlinx.parcelize.Parcelize

@Parcelize
data class PONativeAlternativePaymentMethodConfiguration(
    val gatewayConfigurationId: String,
    val invoiceId: String,
    val options: Options = Options(),
    val style: Style? = null
) : Parcelable {

    /**
     * @param inlineSingleSelectValuesLimit Defines maximum number of options that will be
     * displayed inline for parameters where user should select single option (e.g. radio buttons).
     * Default value is _5_.
     * @param skipSuccessScreen Only applies when [waitsPaymentConfirmation] is _true_.
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
        val paymentConfirmationTimeoutSeconds: Int = MAX_PAYMENT_CONFIRMATION_TIMEOUT_SECONDS,
        val paymentConfirmationSecondaryAction: SecondaryAction? = null
    ) : Parcelable {
        companion object {
            const val MAX_PAYMENT_CONFIRMATION_TIMEOUT_SECONDS = 180
        }
    }

    sealed class SecondaryAction : Parcelable {
        /**
         * @param text Action text. Pass _null_ to use default text.
         * @param disabledForSeconds Initially disables action for the given amount of time in seconds.
         * By default user can interact with action immediately when it's visible.
         */
        @Parcelize
        data class Cancel(
            val text: String? = null,
            val disabledForSeconds: Int = 0
        ) : SecondaryAction()
    }

    @Parcelize
    data class Cancellation(
        val dragDown: Boolean = true,
        val touchOutside: Boolean = true,
        val backPressed: Boolean = true
    ) : Parcelable

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
        val message: POTextStyle? = null,
        val successMessage: POTextStyle? = null,
        @DrawableRes
        val successImageResId: Int? = null,
        @Deprecated("Use property 'background: POBackgroundStyle'.")
        val backgroundDecoration: POBackgroundDecorationStyle? = null
    ) : Parcelable
}
