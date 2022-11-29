package com.processout.sdk.ui.nativeapm

import android.os.Parcelable
import com.processout.sdk.ui.shared.style.POButtonStyle
import com.processout.sdk.ui.shared.style.POInputStyle
import com.processout.sdk.ui.shared.style.POTextStyle
import kotlinx.parcelize.Parcelize
import java.text.NumberFormat

@Parcelize
data class PONativeAlternativePaymentMethodConfiguration(
    val gatewayConfigurationId: String,
    val invoiceId: String,
    val viewType: ViewType = ViewType.BOTTOM_SHEET,
    val options: Options? = null,
    val style: Style? = null
) : Parcelable {

    @Parcelize
    enum class ViewType : Parcelable {
        BOTTOM_SHEET,
        FULLSCREEN
    }

    @Parcelize
    data class Options(
        val title: String? = null,
        val currencyFormat: NumberFormat? = null,
        val isBottomSheetCancelableOnOutsideTouch: Boolean = true
    ) : Parcelable

    @Parcelize
    data class Style(
        val textStyle: POTextStyle? = null,
        val inputStyle: POInputStyle? = null,
        val buttonStyle: POButtonStyle? = null
    ) : Parcelable
}
