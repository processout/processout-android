package com.processout.sdk.ui.nativeapm

import android.os.Parcelable
import androidx.annotation.ColorInt
import com.processout.sdk.ui.shared.style.POTextStyle
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

    @Parcelize
    data class Options(
        val title: String? = null,
        val buttonTitle: String? = null,
        val successMessage: String? = null,
        val isBottomSheetCancelableOnOutsideTouch: Boolean = true,
        val waitsPaymentConfirmation: Boolean = true,
        val paymentConfirmationTimeoutSeconds: Int = 180
    ) : Parcelable

    @Parcelize
    data class Style(
        val title: POTextStyle? = null,
        val input: POInputStyle? = null,
        val button: POButtonStyle? = null,
        @ColorInt
        val backgroundColor: Int? = null,
        @ColorInt
        val progressIndicatorColor: Int? = null,
        val confirmationPromptMessage: POTextStyle? = null,
        val backgroundDecoration: BackgroundDecorationStyle? = null,
        val successMessage: POTextStyle? = null,
        val successBackgroundDecoration: BackgroundDecorationStyle? = null
    ) : Parcelable {

        @Parcelize
        data class BackgroundDecorationStyle(
            @ColorInt
            val primaryColor: Int,
            @ColorInt
            val secondaryColor: Int
        ) : Parcelable
    }
}
