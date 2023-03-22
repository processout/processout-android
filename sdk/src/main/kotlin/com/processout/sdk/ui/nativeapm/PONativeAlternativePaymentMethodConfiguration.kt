package com.processout.sdk.ui.nativeapm

import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.processout.sdk.ui.shared.style.POTextStyle
import com.processout.sdk.ui.shared.style.background.POBackgroundDecorationStyle
import com.processout.sdk.ui.shared.style.button.POButtonStyle
import com.processout.sdk.ui.shared.style.input.POInputStyle
import kotlinx.parcelize.Parcelize

@Parcelize
data class PONativeAlternativePaymentMethodConfiguration(
    val gatewayConfigurationId: String,
    val invoiceId: String,
    val options: Options = Options(),
    val style: Style? = null
) : Parcelable {

    /**
     * @param skipSuccessScreen Only applies when [waitsPaymentConfirmation] is _true_.
     */
    @Parcelize
    data class Options(
        val title: String? = null,
        val primaryActionText: String? = null,
        val secondaryAction: SecondaryAction? = null,
        val cancellation: Cancellation = Cancellation(),
        val successMessage: String? = null,
        val skipSuccessScreen: Boolean = false,
        val waitsPaymentConfirmation: Boolean = true,
        val paymentConfirmationTimeoutSeconds: Int = MAX_PAYMENT_CONFIRMATION_TIMEOUT_SECONDS
    ) : Parcelable {
        companion object {
            const val MAX_PAYMENT_CONFIRMATION_TIMEOUT_SECONDS = 180
        }
    }

    sealed class SecondaryAction : Parcelable {
        @Parcelize
        data class Cancel(val text: String? = null) : SecondaryAction()
    }

    @Parcelize
    data class Cancellation(
        val dragDown: Boolean = true,
        val touchOutside: Boolean = true
    ) : Parcelable

    @Parcelize
    data class Style(
        val title: POTextStyle? = null,
        val input: POInputStyle? = null,
        val codeInput: POInputStyle? = null,
        val primaryButton: POButtonStyle? = null,
        val secondaryButton: POButtonStyle? = null,
        @ColorInt
        val backgroundColor: Int? = null,
        @ColorInt
        val progressIndicatorColor: Int? = null,
        val message: POTextStyle? = null,
        val successMessage: POTextStyle? = null,
        @DrawableRes
        val successImageResId: Int? = null,
        val backgroundDecoration: POBackgroundDecorationStyle? = null
    ) : Parcelable
}
