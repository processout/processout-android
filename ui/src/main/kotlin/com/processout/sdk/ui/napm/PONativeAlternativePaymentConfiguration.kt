package com.processout.sdk.ui.napm

import android.os.Parcelable
import androidx.annotation.ColorRes
import com.processout.sdk.ui.core.style.POActionsContainerStyle
import com.processout.sdk.ui.core.style.PODropdownMenuStyle
import com.processout.sdk.ui.core.style.POFieldStyle
import com.processout.sdk.ui.core.style.POTextStyle
import kotlinx.parcelize.Parcelize

@Parcelize
data class PONativeAlternativePaymentConfiguration(
    val gatewayConfigurationId: String,
    val invoiceId: String,
    val style: Style? = null
) : Parcelable {

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
