package com.processout.sdk.ui.checkout

import android.os.Parcelable
import com.processout.sdk.ui.core.style.POFieldStyle
import kotlinx.parcelize.Parcelize

@Parcelize
data class PODynamicCheckoutConfiguration(
    val invoiceId: String
) : Parcelable {

    @Parcelize
    data class Style(
        val field: POFieldStyle? = null
    ) : Parcelable
}
